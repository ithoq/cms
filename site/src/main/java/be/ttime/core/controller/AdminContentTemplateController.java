package be.ttime.core.controller;

import be.ttime.core.error.ForbiddenException;
import be.ttime.core.persistence.model.ContentTemplateEntity;
import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import be.ttime.core.persistence.model.FieldsetEntity;
import be.ttime.core.persistence.model.InputDataEntity;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.persistence.service.IContentFieldService;
import be.ttime.core.persistence.service.IContentTemplateFieldsetService;
import be.ttime.core.persistence.service.IContentTemplateService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/contentTemplate")
@Slf4j
public class AdminContentTemplateController {

    @Autowired
    private IContentFieldService contentFieldService;
    @Autowired
    private IContentTemplateService contentTemplateService;
    @Autowired
    private IContentTemplateFieldsetService contentTemplateFieldsetService;
    @Autowired
    private IBlockService blockService;

    private final static String VIEWPATH = "admin/contentTemplate/";

    @RequestMapping(value= "", method = RequestMethod.GET)
    public String home(ModelMap model) {
        return VIEWPATH + "home";
    }

    @RequestMapping(value= "/edit", method = RequestMethod.GET)
    public String edit(ModelMap model) {

        return edit(null, model);
    }

    @RequestMapping(value= "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") Long id, ModelMap model) {

        List<FieldsetEntity> all = contentFieldService.findAll();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonFields =  gson.toJson(all);
        model.put("jsonFields", jsonFields);
        String result;

        if(id != null){
            ContentTemplateEntity template = contentTemplateService.findWithFieldsetAndData(id);
            model.put("contentTemplate", template);
            model.put("fieldsetData", gson.toJson(template.getContentTemplateFieldset()));
        }

        return VIEWPATH + "edit";
    }

    @RequestMapping(value= "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String edit(String names, String namespaces, String template, String fieldsets, String inputsData, String contentFieldsetId) {

        // GET FORM VALUES
        Gson gson = new GsonBuilder().create();
        Type dataListType = new TypeToken<ArrayList<ArrayList<InputDataEntity>>>() {}.getType();
        Type fieldsetListType = new TypeToken<ArrayList<FieldsetEntity>>() {}.getType();
        Type stringArrayType = new TypeToken<Collection<String>>(){}.getType();
        Type longArrayType = new TypeToken<Collection<Long>>(){}.getType();

        List<List<InputDataEntity>> dataList = gson.fromJson(inputsData, dataListType);
        List<FieldsetEntity> fieldsetList = gson.fromJson(fieldsets, fieldsetListType);
        List<String> nameList = gson.fromJson(names, stringArrayType);
        List<String> namespaceList = gson.fromJson(namespaces, stringArrayType);
        List<Long> contentFieldsetIdList = gson.fromJson(contentFieldsetId, longArrayType);

        ContentTemplateEntity contentForm =  gson.fromJson(template, ContentTemplateEntity.class);

        ContentTemplateEntity content;
        if(contentForm.getId() != 0){
            content = contentTemplateService.findWithFieldsetAndData(contentForm.getId());
            if(content == null){
                throw new ForbiddenException("Invalid content id : " + contentForm.getId());
            }
        } else {
            content = new ContentTemplateEntity();
        }

        content.setName(contentForm.getName());
        content.setDescription(contentForm.getDescription());
        // save
        if(content.getId() == 0) {
           content = contentTemplateService.save(content);
        }

        List<ContentTemplateFieldsetEntity> contentfieldset = new ArrayList<>();

        for (int i = 0; i < nameList.size() ; i++) {
            ContentTemplateFieldsetEntity cf;
            if(contentFieldsetIdList.get(i) != 0){
                cf = contentTemplateFieldsetService.find(contentFieldsetIdList.get(i));
                if(cf == null){
                    throw new ForbiddenException("Invalid contentfieldset id : " + contentForm.getId());
                }
            } else{
                cf = new ContentTemplateFieldsetEntity();
            }

            List<InputDataEntity> dlist = dataList.get(i);
            for (InputDataEntity inputDataEntity : dlist) {
                inputDataEntity.setContentTemplateFieldsetEntity(cf);
                inputDataEntity.setFieldset(fieldsetList.get(i));
            }

            cf.setDataEntities(dataList.get(i));
            cf.setNamespace(namespaceList.get(i));
            cf.setName(nameList.get(i));
            cf.setPosition(i);
            cf.setContentTemplate(content);
            cf.setFieldset(fieldsetList.get(i));
            contentfieldset.add(cf);
        }

        contentfieldset = contentTemplateFieldsetService.save(contentfieldset);
        content.setContentTemplateFieldset(contentfieldset);
        content = contentTemplateService.save(content);

        return Long.toString(content.getId());
    }
}
