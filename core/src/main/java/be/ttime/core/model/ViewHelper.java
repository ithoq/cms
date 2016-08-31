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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    public Map<String, List<FileEntity>> getDownloadFileByGroupMap(ContentDataEntity data){
        Map<String, List<FileEntity>> result = new HashMap<>();
        for (FileEntity f : data.getContentFiles()) {
            if(f.getFileType().equals("D")){
                String group = f.getFileGroup();
                //if(group == null) group = "default";
                List<FileEntity> list = result.get(group);
                if(list == null){
                    list = new ArrayList<>();
                }
                list.add(f);
                result.put(group, list);
            }
        }
        return result;
    }

    public List<FileEntity> getDownloadFile(ContentDataEntity data){
        List<FileEntity> result;
        result = new ArrayList<>();
        result.addAll(data.getContentFiles().stream().filter(f -> f.getFileType().equals("D")).collect(Collectors.toList()));
        return result;
    }

    public String getBlock(String id){
        BlockEntity blockEntity = blockService.find(id);
        return blockEntity == null ? null : blockEntity.getContent();
    }

    public PageableResult<ContentEntity> findWebContent(String locale, Date begin, Date end, String name, String type, String category, String tags, String contentType, Long pageNumber, Long limit){

        PageableResult<ContentEntity> result = contentService.findWebContent(locale, begin, end, name, type, category, tags, contentType, pageNumber, limit);
        return result;
    }

    public PageableResult<ContentEntity> findWebContent(String locale, Date begin, Date end, String name, String type, String category, String tags, String contentType, String pageNumber, Long limit){

        return findWebContent(locale, begin, end, name, type, category, tags, contentType, Long.parseLong(pageNumber),  limit);
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

    public List<ContentDataEntity> getBrothersData(ContentEntity entity, String code, boolean selfInclude){
        List<ContentDataEntity> result = new ArrayList<>();
        ContentEntity parent = entity.getContentParent();
        ContentDataEntity data;
        for (ContentEntity c : contentService.findByContentParentOrderByOrderAsc(parent)) {
            if(!selfInclude) {
                if (c.getId() == entity.getId())
                    continue;
            }
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

    public ContentDataEntity getNextBrotherData(ContentEntity entity, String code) {
        ContentEntity parent = entity.getContentParent();
        ContentDataEntity nextData = null;
        boolean next = false;
        ContentDataEntity data;
        for (ContentEntity c : contentService.findByContentParentOrderByOrderAsc(parent)) {
            if (c.getId() == entity.getId()) {
                next = true;
                continue;
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

    public String getPageableNavigation(PageableResult result, HttpServletRequest request, Long delta){
        return getPageableNavigation(result, request, delta, false, "pagination");
    }

    public String getPageableNavigation(PageableResult result, HttpServletRequest request, Long delta, boolean limit){
        return getPageableNavigation(result, request, delta, limit, "pagination");
    }
    public String getPageableNavigation(PageableResult pageableResult, HttpServletRequest request, Long delta, boolean limit, String ulClass){

        StringBuilder sb = new StringBuilder();
        String queryString = request.getQueryString();
        Map<String, String> map;
        if(!StringUtils.isEmpty(queryString)){
            map = CmsUtils.queryStringToMap(queryString);
        } else {
            map = new HashMap<>();
        }

        int currentPage = Math.toIntExact(pageableResult.getCurrentPage());
        int totalPage = Math.toIntExact(pageableResult.getTotalPage());
        int arrInf = (int)Math.floor(delta/2);

        int min = 1;
        int max = totalPage;

        if(currentPage >= delta){
            min = currentPage - arrInf;
        }
        max = Math.toIntExact(min + (delta-1));
        if(max > totalPage){
            int diff = max - totalPage;
            max = totalPage;
            min = min - diff;
        }
        if(min < 1) min = 1;

        boolean btnPrevious = false;
        boolean btnFirst = false;
        if(min > 1){
            btnPrevious = true;
        }
        if(min > 2){
            btnFirst = true;
        }
        boolean btnNext = false;
        boolean btnEnd = false;
        if(max < totalPage){
            btnNext = true;
        }
        if(max < totalPage-1){
            btnEnd = true;
        }

        //String resultQueryString = CmsUtils.mapToQueryString(map);
        sb.append("<nav role=\"navigation\"><ul class=\"" + ulClass + "\">");
        if(btnFirst){
            addPaginationLi(sb, -1, 1, map, "first", "...");
        }
        if(btnPrevious){
            addPaginationLi(sb, -1, currentPage - 1 , map, "previous", "<");
        }
        boolean activePage;
        for(; min < (max+1) ; min++){
            addPaginationLi(sb, currentPage, min, map, null, null);
        }
        if(btnNext){
            addPaginationLi(sb, -1, currentPage - 1 , map, "next", ">");
        }
        if(btnEnd){
            addPaginationLi(sb, -1, totalPage, map, "last", "...");
        }
        sb.append("</ul></nav>");

        return sb.toString();
    }

    private void addPaginationLi(StringBuilder sb, int currentPage, int page, Map<String, String> queryMap, String extraClass, String value ){
        boolean activePage = false;
        if(page == currentPage){
            activePage = true;
        }

        sb.append("<li><a href=\"?");
        queryMap.put("page", String.valueOf(page));
        sb.append(CmsUtils.mapToQueryString(queryMap));
        sb.append("\"");
        if(!StringUtils.isEmpty(extraClass)){
            sb.append(" class=\"" + extraClass + "\"");
        } else if(activePage) {
            sb.append(" class=\"active\"");
        }

        sb.append(">");
        if(StringUtils.isEmpty(value)) {
            sb.append(page);
        }
        else{
            sb.append(value);
        }
        sb.append("</a></li>");
    }

}
