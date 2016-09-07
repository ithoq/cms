package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.field.PageData;
import be.ttime.core.model.form.AdminWebContentForm;
import be.ttime.core.persistence.model.*;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.persistence.service.IContentTemplateService;
import be.ttime.core.persistence.service.ITaxonomyService;
import be.ttime.core.util.CmsUtils;
import com.github.slugify.Slugify;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/admin/webContent")
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN_WEBCONTENT')")
public class AdminWebContentController {

    private final static String VIEWPATH = "admin/webContent/";

    @Autowired
    private IContentService contentService;
    @Autowired
    private IApplicationService applicationService;
    @Autowired
    private ITaxonomyService taxonomyService;
    @Autowired
    private IContentTemplateService contentTemplateService;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model, HttpServletRequest request) throws Exception{


        final List<TaxonomyTermEntity> taxonomyTypeList = taxonomyService.findByType("TYPE");
        // Get parameter
        Map<String, Object> map = new HashMap<>();
        boolean isPrivate = false;
        Integer year=  null;
        String lang = StringUtils.trimToNull(request.getParameter("lang"));
        if(lang!= null && !applicationService.getLanguagesMap().containsKey(lang)) lang = null;
        String theme =  StringUtils.trimToNull(request.getParameter("theme"));
        if(theme != null && theme.equals("all")) theme = null;
        String tag =  StringUtils.trimToNull(request.getParameter("tag"));
        if(tag != null && tag.equals("all")) tag = null;
        String type = StringUtils.trimToNull(request.getParameter("type"));

        if(type == null){
            TaxonomyTermEntity taxonomyTermEntity = taxonomyTypeList.get(0);
            if(taxonomyTermEntity != null){
                type = taxonomyTermEntity.getName();
            }
        }
        else if(type.equals("all")){
            type = null;
        }
        String contentPrivate = StringUtils.trimToNull(request.getParameter("private"));
        if(contentPrivate != null && contentPrivate.equals("yes")) isPrivate = true;

        String yearString = StringUtils.trimToNull(request.getParameter("year"));
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if(yearString != null && !yearString.equals("all")){
            try {
                year = Integer.parseInt(yearString);
            } catch(NumberFormatException e){
                year = currentYear;
            }
        } else if(yearString != null && yearString.equals("all")){
            year = null;
        } else if (StringUtils.isEmpty(yearString)){
            year = currentYear;
        }

        List<String> years = new ArrayList<>();
        for(; currentYear >= 2011; currentYear--){
            years.add(Integer.toString(currentYear));
        }

        map.put("lang", lang);
        map.put("theme", theme);
        map.put("tag", tag);
        map.put("year", year);
        map.put("type", type);
        map.put("isPrivate", isPrivate);

        Gson gson = new Gson();
        model.put("tags", taxonomyService.findByType("TAG"));
        model.put("types", taxonomyTypeList);
        model.put("themes", taxonomyService.findByType("THEME"));
        model.put("jsonQuery", gson.toJson(map));
        model.put("years", years);

        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") Long id, String lang, ModelMap model) throws Exception{


        ContentEntity content = null;
        ContentDataEntity contentData = null;
        ContentTemplateEntity template = null;
        ApplicationLanguageEntity appLanguage = null;

        if(id != 0){
            content =  contentService.findContentAdmin(id);
            if(content == null){
                throw new ResourceNotFoundException();
            }
        } else {
            content = new ContentEntity();
            content.setContentType(new ContentTypeEntity("WEBCONTENT"));
            ContentTemplateEntity c = contentTemplateService.findByName("Webcontent");
            content.setContentTemplate(c);
        }
        if(!StringUtils.isEmpty(lang)){
            appLanguage = applicationService.getSiteApplicationLanguageMap().get(lang);
            if (appLanguage == null) {
                log.debug("incorrect language : " + lang + " for the content with id : " + content.getId());
                appLanguage = applicationService.getDefaultSiteApplicationLanguage();
            }
        } else{
            appLanguage = applicationService.getDefaultSiteApplicationLanguage();

            contentData = content.getContentDataList().get(appLanguage.getLocale());
            if (contentData == null && content.getContentDataList().size() > 0) {
                Map.Entry<String, ContentDataEntity> entry = content.getContentDataList().entrySet().iterator().next();
                contentData = entry.getValue();
                appLanguage = applicationService.getSiteApplicationLanguageMap().get(entry.getKey());
            }

        }
        List<String> tags = new ArrayList<>();
        List<String> themes= new ArrayList<>();
        String taxonomyType = "";
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        if(content.getId() != 0){
            contentData = content.getContentDataList().get(appLanguage.getLocale());
            if(contentData == null){
                contentData = new ContentDataEntity();
                contentData.setContent(content);
                contentData.setLanguage(applicationService.getSiteApplicationLanguageMap().get(lang));
                content.addContentData(contentData);
                contentService.saveContent(content);
                content = contentService.findContentAdmin(content.getId());
                contentData = content.getContentDataList().get(appLanguage.getLocale());
            }
            HashMap<String, Object> data = null;
            if (!StringUtils.isEmpty(contentData.getData())) {
                data = CmsUtils.parseData(contentData.getData());
            } else{
                data = new HashMap<>();
            }

            for (TaxonomyTermEntity t : content.getTaxonomyTermEntities()) {
                String type = t.getTaxonomyType().getName();
                if (type.equals("TAG")){
                    tags.add(t.getName());

                } else if(type.equals("THEME")){
                    themes.add(t.getName());
                } else if(type.equals("TYPE")){
                    taxonomyType = t.getName();
                }
            }
            SimpleDateFormat sd = new SimpleDateFormat(CmsUtils.DATETIME_FORMAT);
            if(content.getBeginDate() != null){
                String dateString = sd.format(content.getBeginDate());
                String[] date = dateString.split(" ");
                data.put("date_begin_date", date[0]);
                data.put("date_begin_time", date[1].substring(0, date[1].length()-3));

            }
            if(content.getEndDate() != null){
                String dateString = sd.format(content.getEndDate());
                String[] date = dateString.split(" ");
                data.put("date_end_date", date[0]);
                data.put("date_end_time", date[1].substring(0, date[1].length()-3));
            }
            model.put("dataDate", data);
            template = contentTemplateService.find(content.getContentTemplate().getId());
            model.put("data", CmsUtils.parseStringToPageDate(contentData.getData()));

        }
        else{
            // ADD
            contentData = new ContentDataEntity();
            contentData.setContent(content);
            contentData.setLanguage(applicationService.getSiteApplicationLanguageMap().get(lang));
            content.addContentData(contentData);
            template = contentTemplateService.findByName("Webcontent");
            content.setContentTemplate(template);
        }

        model.put("template", template);
        model.put("initialTags", gson.toJson(tags));
        model.put("initialThemes", gson.toJson(themes));
        model.put("content", content);
        model.put("contentData", contentData);
        model.put("contentLocale", lang);
        model.put("contentType", "WEBCONTENT");
        model.put("type", taxonomyType);
        model.put("tags", taxonomyService.findByTypeJson("TAG"));
        model.put("themes", taxonomyService.findByTypeJson("THEME"));
        model.put("types", taxonomyService.findByType("TYPE"));
        return VIEWPATH + "edit";
    }



    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Transactional
    public String save(@Valid AdminWebContentForm form, BindingResult result, MultipartHttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception{
        ContentEntity content = null;
        ContentDataEntity contentData = null;

        // if errors
        if (result.hasErrors()) {} else{

            Date begin = CmsUtils.parseDate(form.getDateBegin(), form.getDateTimeBegin());
            Date end =  CmsUtils.parseDate(form.getDateEnd(),form.getDateTimeEnd());
            Slugify slugify = new Slugify();

            // get content
            if(form.getContentId() == 0){
                content = new ContentEntity();
                content.setContentType(new ContentTypeEntity(form.getContentType()));
                ContentTemplateEntity c = contentTemplateService.findByName("Webcontent");
                if(c == null){
                    throw new Exception("Content Template Webcontent not exist");
                }
                content.setContentTemplate(c);
            }
            else{
                content = contentService.findContent(form.getContentId());
            }

            if(form.getSelectLanguage().equals(applicationService.getDefaultSiteLang()) ||
                    StringUtils.isEmpty(content.getName()) ){
                content.setName(form.getName());
            }
            // get content data
            if(form.getContentDataId() == 0){
                contentData = new ContentDataEntity();
                contentData.setContent(content);
                contentData.setLanguage(applicationService.getApplicationLanguagesMap().get(form.getSelectLanguage()));
                content.getContentDataList().put(contentData.getLanguage().getLocale(), contentData);
            } else{
                contentData = contentService.findContentData(form.getContentDataId());
            }

            content.setBeginDate(begin);
            content.setEndDate(end);
            content.setMenuItem(false);
            content.setEnabled(form.isEnabled());
            content.setMemberOnly(form.isMemberOnly());

            contentData.setEnabled(form.isContentDataEnabled());
            contentData.setTitle(form.getPageDataTitle());
            contentData.setIntro(form.getIntro());
            contentData.setSlug(slugify.slugify(form.getSlug()));
            contentData.setComputedSlug(getComputedSlug(form.getType(), form.getSlug(), begin, contentData.getLanguage().getLocale(), applicationService.getApplicationConfig().isForcedLangInUrl()));

            // Form data
            Map<String, String> data = new HashMap<>();

            if(form.getThumbnail() != null){
                // new or replace
                if( !form.getThumbnail().isEmpty()){
                    File uploadedFile = CmsUtils.uploadFile(form.getThumbnail(), false, form.getContentType().toLowerCase() + "/" + form.getContentId());
                    String resultPath = CmsUtils.getFilePath(uploadedFile, "public") + "/" + uploadedFile.getName();
                    //data.put("image_preview", resultPath.substring(1));
                    content.setImage(resultPath.substring(1));
                }
                // existing
                //else{
                //    data.put("image_preview", form.getPreviousFile());
                //}
            }
            ContentTemplateEntity ct = contentTemplateService.find(content.getContentTemplate().getId());
            PageData pageData = CmsUtils.fillData(ct.getContentTemplateFieldset(), request);
            //data.put("dev_top", form.getDevIncludeTop());
            //data.put("dev_bot", form.getDevIncludeBot());

            content.setIncludeTop(form.getDevIncludeTop());
            content.setIncludeBottom(form.getDevIncludeBot());

            data.put("seo_h1", form.getSeoH1());
            data.put("seo_description", form.getSeoDescription());
            data.put("seo_tags", form.getSeoTag());

            pageData.getDataString().putAll(data);
            Gson gson = new GsonBuilder().disableHtmlEscaping().setDateFormat(CmsUtils.DATETIME_FORMAT).create();
            String json = gson.toJson(pageData);
            contentData.setData(json);

            // Taxonomy
            List<TaxonomyTermEntity> taxonomyTermEntityList = new ArrayList<>();
            TaxonomyTermEntity taxonomyType = taxonomyService.findByType(form.getType(), "TYPE");
            if(taxonomyType == null){
                throw new IllegalArgumentException("type not found : " + form.getType());
            }
            taxonomyTermEntityList.add(taxonomyType);

            if(!StringUtils.isEmpty(form.getTags())){
                String[] tags = form.getTags().split(",");
                if(tags.length > 0){
                    taxonomyTermEntityList.addAll(taxonomyService.createIfNotExist(Arrays.asList(tags), "TAG"));
                }
            }
            if(!StringUtils.isEmpty(form.getThemes())){
                String[] themes = form.getThemes().split(",");
                if(themes.length > 0){
                    taxonomyTermEntityList.addAll(taxonomyService.createIfNotExist(Arrays.asList(themes), "THEME"));
                }
            }
            Set<TaxonomyTermEntity> terms = new HashSet<>();
            for (TaxonomyTermEntity t : taxonomyTermEntityList) {
                if(t != null) {
                    terms.add(t);
                }
            }
            //Set<TaxonomyTermEntity> terms = new HashSet<>(taxonomyTermEntityList);
            content.setTaxonomyTermEntities(terms);

            // Save
            content = contentService.saveContent(content);
            contentData = contentService.saveContentData(contentData);

        }

        return "redirect:/admin/webContent/edit/" +
                content.getId() +
                "?lang=" +
                form.getSelectLanguage();
    }


    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(String params) throws Exception {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> map = gson.fromJson(params, type);

        for (Map.Entry<String, String> entry : map.entrySet())
        {
            if(entry.getValue() != null && entry.getValue().equals("all")){
                map.put(entry.getKey(), null);
            }
        }

        return contentService.getContentJsonByTypeAndParams("WEBCONTENT", map);
        //return contentService.("all");
    }

    private String getComputedSlug(String type, String slug, Date dateCreated, String locale, boolean forceLangInUrl) {
        StringBuilder computedSlug = new StringBuilder();
        DateTime dateTime = new DateTime(dateCreated);

        if(StringUtils.isEmpty(slug)) throw new IllegalArgumentException("slug is null");

        if(!slug.startsWith("/")){
            slug = "/" + slug;
        }
        if(forceLangInUrl){
            computedSlug.append("/").append(locale);
        }
        Slugify slf = null;
        try {
             slf = new Slugify();
        } catch(IOException e){}

        computedSlug.append("/")
                    .append(slf.slugify(type.toLowerCase()))
                    .append("/");

        int day = dateTime.getDayOfMonth();
        int month = dateTime.getMonthOfYear();
        int year = dateTime.getYear();

        computedSlug.append(year)
                    .append("/")
                    .append(CmsUtils.twoDigit(month))
                    .append("/")
                    .append(CmsUtils.twoDigit(day))
                    .append(slug);

        return computedSlug.toString();
    }
}
