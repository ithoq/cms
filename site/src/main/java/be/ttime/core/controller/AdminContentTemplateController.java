package be.ttime.core.controller;

import be.ttime.core.error.ForbiddenException;
import be.ttime.core.persistence.model.*;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.persistence.service.IContentTemplateService;
import be.ttime.core.persistence.service.IFieldsetService;
import be.ttime.core.util.CmsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/contentTemplate")
@Slf4j
@PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
public class AdminContentTemplateController {

    @Autowired
    private IContentTemplateService contentTemplateService;
    @Autowired
    private IFieldsetService fieldsetService;
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

        List<FieldsetEntity> all = fieldsetService.findAllFieldset();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonFields =  gson.toJson(all);
        model.put("jsonFields", jsonFields);
        String result;

        if(id != null){
            ContentTemplateEntity template = contentTemplateService.find(id);
            model.put("contentTemplate", template);
            model.put("fieldsetData", gson.toJson(template.getContentTemplateFieldset()));
        }

        return VIEWPATH + "edit";
    }

    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(HttpServletResponse response) {

        return contentTemplateService.jsonContent();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") Long id, HttpServletResponse response) {

        if (id == null) {
            response.setStatus(500);
        }
        try {
            contentTemplateService.delete(id);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value= "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String edit(HttpServletResponse response, String names, String namespaces, String arrays, String template, String fieldsets, String inputsData, String contentFieldsetId, String blockData) {

        // GET FORM VALUES
        Gson gson = new GsonBuilder().create();
        Type dataListType = new TypeToken<ArrayList<ArrayList<InputDataEntity>>>() {}.getType();
        Type fieldsetListType = new TypeToken<ArrayList<FieldsetEntity>>() {}.getType();
        Type stringArrayType = new TypeToken<Collection<String>>(){}.getType();
        Type booleanArrayType = new TypeToken<Collection<Boolean>>(){}.getType();
        Type longArrayType = new TypeToken<Collection<Long>>(){}.getType();

        List<List<InputDataEntity>> dataList = gson.fromJson(inputsData, dataListType);
        List<FieldsetEntity> fieldsetList = gson.fromJson(fieldsets, fieldsetListType);
        List<String> nameList = gson.fromJson(names, stringArrayType);
        List<String> namespaceList = gson.fromJson(namespaces, stringArrayType);
        List<Boolean> isArrayList = gson.fromJson(arrays, booleanArrayType);
        List<Long> contentFieldsetIdList = gson.fromJson(contentFieldsetId, longArrayType);

        ContentTemplateEntity contentForm =  gson.fromJson(template, ContentTemplateEntity.class);
        BlockEntity blockPosted =  gson.fromJson(blockData, BlockEntity.class);

        ContentTemplateEntity content;
        BlockEntity block = null;

        if(contentForm.getId() != 0){
            content = contentTemplateService.find(contentForm.getId());
            if(content == null){
                response.setStatus(500);
                throw new ForbiddenException("Invalid content id : " + contentForm.getId());
            }
            if(StringUtils.isEmpty(blockPosted.getName())){
                response.setStatus(500);
                throw new ForbiddenException("Block name can't be null");
            }

            block = blockService.find(blockPosted.getName());
        } else {
            content = new ContentTemplateEntity();
        }
        if(block == null){
            block = new BlockEntity();
            block.setName(blockPosted.getName());
            block.setBlockType(new BlockTypeEntity(CmsUtils.BLOCK_TYPE_CONTENT_TEMPLATE));
        }

        block.setDynamic(true);
        block.setEnabled(true);

        block.setDisplayName(blockPosted.getDisplayName());
        block.setContent(blockPosted.getContent());

        content.setContentType(new ContentTypeEntity(CmsUtils.CONTENT_TYPE_PAGE));

        content.setActive(contentForm.isActive());
        content.setName(contentForm.getName());
        content.setDescription(contentForm.getDescription());
        content.setIncludeTop(contentForm.getIncludeTop());
        content.setIncludeBottom(contentForm.getIncludeBottom());
        content.setDeletable(true);
        // save
        if(content.getId() == 0) {
           content = contentTemplateService.save(content);
        }

        List<ContentTemplateFieldsetEntity> contentfieldset = new ArrayList<>();

        for (int i = 0; i < nameList.size() ; i++) {
            ContentTemplateFieldsetEntity cf;
            if(contentFieldsetIdList.get(i) != 0){
                cf = fieldsetService.findContentTemplateFieldset(contentFieldsetIdList.get(i));
                if(cf == null){
                    response.setStatus(500);
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
            cf.setArray(isArrayList.get(i));
            cf.setName(nameList.get(i));
            cf.setPosition(i);
            cf.setContentTemplate(content);
            cf.setFieldset(fieldsetList.get(i));
            contentfieldset.add(cf);
        }

        block = blockService.save(block);
        content.setBlock(block);
        contentfieldset = fieldsetService.saveContentTemplateFieldset(contentfieldset);
        content.setContentTemplateFieldset(contentfieldset);
        content = contentTemplateService.save(content);

        return Long.toString(content.getId());
    }
}
