package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.RedirectMessage;
import be.ttime.core.model.form.AdminFielsetForm;
import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.model.BlockTypeEntity;
import be.ttime.core.persistence.model.FieldsetEntity;
import be.ttime.core.persistence.model.InputDefinitionEntity;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.persistence.service.IFieldsetService;
import be.ttime.core.util.CmsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/fieldset")
@Slf4j
@PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
public class AdminFieldsetController {

    @Autowired
    private IFieldsetService fieldsetService;
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
    public String edit(@PathVariable("id") Long id,ModelMap model, @ModelAttribute("redirectMessage") RedirectMessage redirectMessage) {
        if(id != null){
            FieldsetEntity fieldset = fieldsetService.findFieldset(id);
            if(fieldset == null){
                throw new ResourceNotFoundException("FieldSet with id \" + id + \" not found!\"");
            }
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            List<InputDefinitionEntity> inputs = fieldset.getInputs();
            String jsonResult = gson.toJson(inputs);
            model.put("redirectMessage", redirectMessage);
            model.put("fieldset", fieldset);
            model.put("inputsJson", jsonResult);
        }
        return VIEWPATH + "edit";
    }


    @RequestMapping(value= "/edit", method = RequestMethod.POST)
    public String edit(ModelMap model, @Valid AdminFielsetForm form, BindingResult result, RedirectAttributes redirectAttributes ) {
        return edit(null, model, form, result,redirectAttributes);
    }

    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(HttpServletResponse response) {

        return fieldsetService.jsonFieldset();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteFielset(@PathVariable("id") Long id, HttpServletResponse response) {

        if (id == null) {
            response.setStatus(500);
        }
        try {
            fieldsetService.deleteFieldset(id);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value= "/edit/{id}", method = RequestMethod.POST)
    public String edit(@PathVariable("id") Long id, ModelMap model, @Valid AdminFielsetForm form, BindingResult result, RedirectAttributes redirectAttributes) throws ResourceNotFoundException {

        FieldsetEntity fieldset;
        BlockEntity blockEntity;
        if(id == null) {
            fieldset = new FieldsetEntity();
            blockEntity = new BlockEntity();
        } else {
            fieldset = fieldsetService.findFieldset(id);
            if(fieldset == null){
                throw new ResourceNotFoundException("FieldSet with id " + id + " not found!");
            }
            blockEntity = fieldset.getBlockEntity();
            if(blockEntity == null) {
                blockEntity = new BlockEntity();
            }
        }

        fieldset.setArray(form.isArray());
        List<InputDefinitionEntity> inputDefinitionEntityList = new ArrayList<>();
        if(inputDefinitionEntityList.size() > 0) {
            throw new AccessDeniedException("Field must have at least one input!");
        }

        for (int i = 0; i < form.getInputsName().length; i++) {
            String validation = form.getInputsValidation()[i];
            if (validation.equals("null")) {
                validation = null;
            }
            inputDefinitionEntityList.add(new InputDefinitionEntity(form.getInputDataId()[i], form.getOrder()[i], form.getInputsName()[i], form.getTypeSelect()[i], validation, fieldset));
        }

        if(StringUtils.isEmpty(blockEntity.getName())) {
            blockEntity.setName(form.getBlockName());
        }

        blockEntity.setDisplayName(form.getBlockDisplayName());
        blockEntity.setBlockType(new BlockTypeEntity(CmsUtils.BLOCK_TYPE_FIELDSET));
        blockEntity.setDeletable(true);
        blockEntity.setEnabled(true);
        blockEntity.setContent(form.getBlockContent());

        blockService.save(blockEntity);

        fieldset.setDescription(form.getFieldsetDescription());
        fieldset.setName(form.getFieldsetName());
        fieldset.setInputs(inputDefinitionEntityList);
        fieldset.setBlockEntity(blockEntity);
        fieldset = fieldsetService.saveFieldset(fieldset);


        //return new RedirectView("/admin/fieldset/edit/" + fieldset.getId(), true, true , false);
        //model.clear();
        RedirectMessage redirectMessage = new RedirectMessage();
        redirectMessage.setType(RedirectMessage.SUCCESS);
        redirectMessage.setMessage("success.general");
        redirectAttributes.addFlashAttribute("redirectMessage", redirectMessage);

        return "redirect:/admin/fieldset/edit/" + fieldset.getId() ;
    }

}
