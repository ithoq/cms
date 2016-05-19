package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.form.AdminFielsetForm;
import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.model.BlockTypeEntity;
import be.ttime.core.persistence.model.FieldsetEntity;
import be.ttime.core.persistence.model.InputDefinitionEntity;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.persistence.service.IContentFieldService;
import be.ttime.core.util.CmsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/fieldset")
@Slf4j
public class AdminFieldsetController {

    @Autowired
    private IContentFieldService contentFieldService;
    @Autowired
    private IBlockService blockService;
    private final static String VIEWPATH = "admin/fieldset/";

    @RequestMapping(value= "", method = RequestMethod.GET)
    public String home(ModelMap model) {
        return VIEWPATH + "home";
    }

    @RequestMapping(value= "/edit", method = RequestMethod.GET)
    public String edit(ModelMap model) {
        return VIEWPATH + "edit";
    }

    @RequestMapping(value= "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") Long id,ModelMap model) {
        if(id != null){
            FieldsetEntity fieldset = contentFieldService.findFieldset(id);
            if(fieldset == null){
                throw new ResourceNotFoundException("FieldSet with id \" + id + \" not found!\"");
            }
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            List<InputDefinitionEntity> inputs = fieldset.getInputs();
            String jsonResult = gson.toJson(inputs);
            model.put("fieldset", fieldset);
            model.put("inputsJson", jsonResult);
        }
        return VIEWPATH + "edit";
    }


    @RequestMapping(value= "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test(ModelMap model) {
        return "Olalal hiho";
    }


    @RequestMapping(value= "/edit", method = RequestMethod.POST)
    public String edit(ModelMap model, @Valid AdminFielsetForm form, BindingResult result) {
        return edit(null, model, form, result);
    }

    @RequestMapping(value= "/edit/{id}", method = RequestMethod.POST)
    public String edit(@PathVariable("id") Long id, ModelMap model, @Valid AdminFielsetForm form, BindingResult result) throws ResourceNotFoundException {

        FieldsetEntity fieldset;
        BlockEntity blockEntity;
        if(id == null) {
            fieldset = new FieldsetEntity();
            blockEntity= new BlockEntity();
        } else {
            fieldset = contentFieldService.findFieldset(id);
            if(fieldset == null){
                throw new ResourceNotFoundException("FieldSet with id " + id + " not found!");
            }
            blockEntity = fieldset.getBlockEntity();
        }

        List<InputDefinitionEntity> inputDefinitionEntityList = new ArrayList<>();
        for (int i = 0; i < form.getInputsName().length ; i++) {
            String validation = form.getInputsValidation()[i];
            if(validation.equals("null")){
                validation = null;
            }
            inputDefinitionEntityList.add(new InputDefinitionEntity(form.getInputDataId()[i], form.getOrder()[i], form.getInputsName()[i], form.getInputsArray()[i], validation, fieldset));
        }

        if(StringUtils.isEmpty(blockEntity.getName())) {
            blockEntity.setName(form.getBlockName());
        }

        blockEntity.setDisplayName(form.getBlockDisplayName());
        blockEntity.setBlockType(new BlockTypeEntity(CmsUtils.BLOCKTYPE_FIELDSET));
        blockEntity.setCacheable(false);
        blockEntity.setDeletable(true);
        blockEntity.setEnabled(true);
        blockEntity.setContent(form.getBlockContent());

        blockService.save(blockEntity);

        fieldset.setDescription(form.getFieldsetDescription());
        fieldset.setName(form.getFieldsetName());
        fieldset.setInputs(inputDefinitionEntityList);
        fieldset.setBlockEntity(blockEntity);
        fieldset = contentFieldService.saveFieldset(fieldset);


        //return new RedirectView("/admin/fieldset/edit/" + fieldset.getId(), true, true , false);
        //model.clear();
        return "redirect:/admin/fieldset/edit/" + fieldset.getId() ;
    }

}