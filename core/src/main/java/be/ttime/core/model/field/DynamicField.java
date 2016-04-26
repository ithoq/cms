package be.ttime.core.model.field;

import be.ttime.core.persistence.model.PageBlockEntity;
import be.ttime.core.persistence.service.IPageBlockService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private IPageBlockService blockService;

    public static boolean isJSONValid(String JSON_STRING) {
        try {
            gson.fromJson(JSON_STRING, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    public String renderField(Field field, PageData pageData) throws Exception {

        PageBlockEntity block = blockService.findByNameAndBlockType(field.getBlockName(), PageBlockEntity.BlockType.FieldSet);

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

        return pebbleUtils.parseBlock(block, model);
    }
}
