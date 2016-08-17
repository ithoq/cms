package be.ttime.core.model;

import be.ttime.core.error.ForbiddenException;
import be.ttime.core.model.field.PageData;
import be.ttime.core.persistence.model.*;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import com.github.slugify.Slugify;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private IApplicationService applicationService;

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

    public Map<String, List<FileEntity>> getFileByGroupMap(ContentDataEntity data){
        Map<String, List<FileEntity>> result = new HashMap<>();
        for (FileEntity f : data.getContentFiles()) {
            if(f.getFileType().getName().equals("DOWNLOAD")){
                List<FileEntity> list = result.get(f.getFileGroup());
                if(list == null){
                    list = new ArrayList<>();
                }
                list.add(f);
                result.put(f.getFileGroup(), list);
            }
        }
        return result;
    }

    public String getBlock(String id){
        BlockEntity blockEntity = blockService.find(id);
        return blockEntity == null ? null : blockEntity.getContent();
    }

    public PageableResult<ContentEntity> findWebContent(String locale, Date begin, Date end, String name, String category, List<String> contentType, Long pageNumber, Long limit, Long offset){


        PageableResult<ContentEntity> result = contentService.findWebContent(locale, begin, end, name, category, contentType, pageNumber, limit, offset);

        return result;
    }

    public String getLanguageName(String code){
        Locale locale = applicationService.getLanguagesMap().get(code);
        return locale.getDisplayName(LocaleContextHolder.getLocale());
    }

    public Locale getLocale(String code){
        return  applicationService.getLanguagesMap().get(code);
    }

    public boolean userHasRole(ContentEntity content){
        return CmsUtils.hasRoles(contentService.getRoleForContent(content));
    }

    public void checkRole(ContentEntity content){
        boolean result = userHasRole(content);
        result = false;
        if(!result){
            throw new ForbiddenException();
        }
    }

    public List<ContentDataEntity> getChildrensData(Long id, String code){
        return getChildrensData(contentService.findContentAdmin(id), code);
    }

    public List<ContentDataEntity> getChildrensData(ContentEntity entity, String code){
        List<ContentDataEntity> result = new ArrayList<>();
        ContentDataEntity data;
        for (ContentEntity c : entity.getContentChildren()) {
            ContentEntity contentAdmin = contentService.findContentAdmin(c.getId());
            data = contentAdmin.getContentDataList().get(code);
            if(data != null) result.add(data);
        }
        return result;
    }

    public List<ContentDataEntity> getBrothersData(ContentEntity entity, String code){
        List<ContentDataEntity> result = new ArrayList<>();
        ContentEntity parent = entity.getContentParent();
        ContentDataEntity data;
        for (ContentEntity c : contentService.findByContentParentOrderByOrderAsc(parent)) {
            if(c.getId() == entity.getId())
                continue;
            ContentEntity contentAdmin = contentService.findContentAdmin(c.getId());
            data = contentAdmin.getContentDataList().get(code);
            if(data != null ) result.add(data);
        }
        return result;
    }

    public List<ContentDataEntity> getParentsData(ContentEntity entity, String code){
        List<ContentDataEntity> result = new ArrayList<>();
        ContentEntity parent = entity.getContentParent();
        if(parent == null) return null;

        if(parent.getContentParent().getId() == 0) {
            parent = null;
        } else {
            parent = contentService.findContentAdmin(parent.getContentParent().getId());
        }

        ContentDataEntity data;
        for (ContentEntity c : contentService.findByContentParentOrderByOrderAsc(parent)) {

            ContentEntity contentAdmin = contentService.findContentAdmin(c.getId());
            data = contentAdmin.getContentDataList().get(code);
            if(data != null) result.add(data);
        }
        return result;
    }

    public ContentDataEntity getNextBrotherData(ContentEntity entity, String code, boolean selfInclude) {
        ContentEntity parent = entity.getContentParent();
        ContentDataEntity nextData = null;
        boolean next = false;
        ContentDataEntity data;
        for (ContentEntity c : contentService.findByContentParentOrderByOrderAsc(parent)) {
            if (!selfInclude) {
                if (c.getId() == entity.getId()) {
                    next = true;
                    continue;
                }
            }

            if (next) {
                ContentEntity contentAdmin = contentService.findContentAdmin(c.getId());
                data = contentAdmin.getContentDataList().get(code);
                if (data != null) {
                    nextData = data;
                    break;
                }
            }
        }
        return nextData;
    }

    public ContentDataEntity getPreviousBrotherData(ContentEntity entity, String code){
        if(entity == null) return null;
        ContentEntity parent = entity.getContentParent();
        ContentDataEntity previousData = null;
        ContentDataEntity data;
        for (ContentEntity c : contentService.findByContentParentOrderByOrderAsc(parent)) {
            if(c.getId() == entity.getId()) {
                break;
            }

            ContentEntity contentAdmin = contentService.findContentAdmin(c.getId());
            data = contentAdmin.getContentDataList().get(code);
            if(data != null) previousData=data;
        }
        return previousData;
    }

    public ContentDataEntity getContentData(Long id, String code){
        ContentEntity contentAdmin = contentService.findContentAdmin(id);
        if (contentAdmin == null) return null;

        return contentAdmin.getContentDataList().get(code);
    }
}
