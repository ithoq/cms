package be.ttime.core.model.field;

import be.ttime.core.persistence.model.ContentTemplateEntity;
import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import be.ttime.core.persistence.model.FieldsetEntity;
import be.ttime.core.persistence.model.InputDataEntity;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import com.github.slugify.Slugify;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/*
    Render a dynamic field in CMS Page administration.
 */
@Component
public class DynamicField {

    private static final String FIELD_VIEW_PATH = "/WEB-INF/views/admin/field/";
    private static final Gson gson = new Gson();
    @Autowired
    private PebbleUtils pebbleUtils;
    @Autowired
    private IBlockService blockService;

    public static boolean isJSONValid(String JSON_STRING) {
        try {
            gson.fromJson(JSON_STRING, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    public String renderField(ContentTemplateEntity template, PageData pageData) throws Exception {

        StringBuilder builder = new StringBuilder();
        Map<String, Object> model;
        Slugify slg = new Slugify();

        for (ContentTemplateFieldsetEntity contentTemplateFieldset : template.getContentTemplateFieldset()) {

            model = new HashMap<>();
            model.put("np",  contentTemplateFieldset.getNamespace() + '_');

            FieldsetEntity fieldset = contentTemplateFieldset.getFieldset();
            Map<String, Object> inputsMap = new HashMap<>();
            SimpleDateFormat dateFormatter = new SimpleDateFormat(CmsUtils.DATE_FORMAT);
            for (InputDataEntity inputDataEntity : contentTemplateFieldset.getDataEntities()) {

                String finalName = contentTemplateFieldset.getNamespace() + "_" + slg.slugify(inputDataEntity.getInputDefinition().getName());
                Map<String, Object> inputMap = new HashMap<>();
                inputMap.put("title", inputDataEntity.getTitle());
                inputMap.put("name", finalName);
                inputMap.put("hint", inputDataEntity.getHint());
                inputMap.put("validation", inputDataEntity.getValidation());
                inputMap.put("default", inputDataEntity.getDefaultValue());
                inputMap.put("isArray", contentTemplateFieldset.isArray());

                String inputType = inputDataEntity.getInputDefinition().getType();
                if(inputDataEntity.getFieldset().isArray()){
                    if(inputType.equals("date")){
                        Date[] dates = pageData.getDataDateArray().get(finalName);
                        if(dates != null) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < pageData.getDataDateArray().get(finalName).length; i++) {
                                if (i != 0) {
                                    sb.append(",");
                                }
                                sb.append(dateFormatter.format(dates[i]));
                            }
                            model.put("data", sb.toString());
                        }
                    } else{
                        model.put("data", Arrays.toString(pageData.getDataStringArray().get(finalName)));
                    }
                } else {
                    if(inputType.equals("date")){
                        Date d = pageData.getDataDate().get(finalName);
                        if(d != null){
                            model.put("data", dateFormatter.format(d));
                        }

                    } else{
                        model.put("data", pageData.getDataString().get(finalName));
                    }
                }

                //TODO: DATA
               // if()

                inputsMap.put(finalName, inputMap);
            }

            model.put("inputs", inputsMap);

            builder.append(pebbleUtils.parseBlock(fieldset.getBlockEntity(), model));
        }

        return builder.toString();



        /*
        BlockEntity block = blockService.findByNameAndBlockType(contentTemplateFieldsetEntity.get.getF.getBlockName(), "FIELDSET");

        if (block == null) {
            return CmsUtils.alert("danger", "unable to find the block named " + field.getBlockName(), "Block name error");
        }
        if (field.getInputs().size() == 0) {
            return CmsUtils.alert("danger", "field " + field.getBlockName() + " must have inputs ", "Block inputs size");
        }

        Map<String, Object> model = new HashMap<>();
        model.put("name", field.getName());
        model.put("desc", field.getDescription());
        model.put("np", field.getNamespace());
        model.put("blockName", field.getBlockName());
        if (pageData != null) {
            model.put("data", pageData.getData());
            model.put("dataArray", pageData.getDataArray());
        }

        Map<String, Object> inputsModel = new HashMap<>();
        List<Input> inputs = field.getInputs();
        if (inputs.size() > 1) {
            for (Input input : inputs) {
                inputsModel.put(field.getNamespace() + '_' + field.getName(), input);
            }
            model.put("inputs", inputsModel);
        } else {
            model.put("input", inputs.get(0));
        }

        return pebbleUtils.parseBlock(block, model);*/
    }
}
