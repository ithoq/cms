package be.ttime.core.model.field;

import be.ttime.core.persistence.model.ContentTemplateEntity;
import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import be.ttime.core.persistence.model.FieldsetEntity;
import be.ttime.core.persistence.model.InputDataEntity;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import com.github.slugify.Slugify;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
    Render a dynamic field in CMS Page administration.
 */
@Component
@Slf4j
public class ViewHelper {

    private static final String FIELD_VIEW_PATH = "/WEB-INF/views/admin/field/";
    private static final Gson gson = new Gson();
    @Autowired
    private PebbleUtils pebbleUtils;
    @Autowired
    private IBlockService blockService;
    @Autowired
    private IContentService contentService;

    public static boolean isJSONValid(String JSON_STRING) {
        try {
            gson.fromJson(JSON_STRING, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    public String renderField(ContentTemplateEntity template, PageData pageData) throws Exception {

        if(template == null){
            return null;
        }
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
                if(pageData!= null) {
                    String inputType = inputDataEntity.getInputDefinition().getType();
                    if (fieldset.isArray() && contentTemplateFieldset.isArray()) {
                        if (inputType.equals("date")) {
                            Date[] dates = pageData.getDataDateArray().get(finalName);
                            if (dates != null) {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < pageData.getDataDateArray().get(finalName).length; i++) {
                                    if (i != 0) {
                                        sb.append(",");
                                    }
                                    sb.append(dateFormatter.format(dates[i]));
                                }
                                inputMap.put("data", sb.toString());
                            }
                        } else {
                            inputMap.put("data", Arrays.toString(pageData.getDataStringArray().get(finalName)));
                        }
                    } else {
                        if (inputType.equals("date")) {
                            Date d = pageData.getDataDate().get(finalName);
                            if (d != null) {
                                inputMap.put("data", dateFormatter.format(d));
                            }

                        } else {
                            inputMap.put("data", pageData.getDataString().get(finalName));
                        }
                    }
                }

                // avoid "null"
                inputMap.putIfAbsent("data", "");

                inputsMap.put(finalName, inputMap);
            }

            model.put("inputs", inputsMap);

            builder.append(pebbleUtils.parseBlock(fieldset.getBlockEntity(), model));
        }

        return builder.toString();
    }

    public String getNavMenu(String locale, Long depth){
        if(depth == null){
            throw new IllegalArgumentException("Nav depth can't bel null");
        }
        String menu = contentService.getNavMenu(locale, depth);
        return menu;
    }
}
