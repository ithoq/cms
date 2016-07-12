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
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/admin/webContent")
@Slf4j
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

    @RequestMapping(value = "{contentType}", method = RequestMethod.GET)
    public String home(@PathVariable("contentType") String contentType, ModelMap model) throws Exception{
        return home(contentType, null, model);
    }

    @RequestMapping(value = "{contentType}/{locale}", method = RequestMethod.GET)
    public String home(@PathVariable("contentType") String contentType, @PathVariable("locale") String locale, ModelMap model) throws Exception{
        if(!contentService.contentTypeExist(contentType)){
            throw new ResourceNotFoundException();
        }

        if(locale == null || applicationService.getSiteLanguagesMap().get(locale) == null){
            locale = applicationService.getDefaultSiteLang();
        }

        model.put("contentType", contentType);
        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/edit/{contentType}/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("contentType") String contentType, @PathVariable("id") Long id, String langCode, ModelMap model) throws Exception{

        if(!contentService.contentTypeExist(contentType)){
            throw new ResourceNotFoundException();
        }
        ContentEntity content = null;
        ContentDataEntity contentData = null;
        ContentTemplateEntity template = null;
        if(id != null && id != 0){
            content =  contentService.findContentAdmin(id);
            if(content == null){
                throw new ResourceNotFoundException();
            }
        }
        if(langCode == null){
            langCode = applicationService.getDefaultSiteLang();
        }
        List<String> tags = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        if(content != null){
            contentData = content.getContentDataList().get(langCode);

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

                } else if(type.equals("CATEGORY")){
                    categories.add(t.getName());
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
            model.put("data", data);
            template = contentTemplateService.find(content.getContentTemplate().getId());
            model.put("pageData", CmsUtils.parseStringToPageDate(contentData.getData()));

        }
        else{
            template = contentTemplateService.findByName("Webcontent");
        }



        model.put("template", template);
        model.put("initialTags", gson.toJson(tags));
        model.put("initialCategories", gson.toJson(categories));
        model.put("content", content);
        model.put("contentData", contentData);
        model.put("contentType", contentType);
        model.put("tags", taxonomyService.findByTypeJson("TAG"));
        model.put("categories", taxonomyService.findByTypeJson("CATEGORY"));
        return VIEWPATH + "edit";
    }



    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Transactional
    public String save(@Valid AdminWebContentForm form, BindingResult result, MultipartHttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception{
        ContentEntity content = null;
        ContentDataEntity contentData = null;

        // if errors

        if (result.hasErrors()) {


        } else{

            Date begin = CmsUtils.parseDate(form.getDateBegin(), form.getDateTimeBegin());
            Date end =  CmsUtils.parseDate(form.getDateEnd(),form.getDateTimeEnd());
            Slugify slugify = new Slugify();


            // get content
            if(form.getContentId() == null){
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
                content.setName(form.getTitle());
            }
            // get content data
            if(form.getContentId() == null || form.getContentDataId() == null){
                contentData = new ContentDataEntity();
                contentData.setContent(content);
                contentData.setLanguage(applicationService.getApplicationLanguagesMap().get(form.getSelectLanguage()));
                content.getContentDataList().put(contentData.getLanguage().getLocale(), contentData);
            } else{
                contentData = contentService.findContentData(form.getContentDataId());
            }

            content.setBeginDate(begin);
            content.setEndDate(end);

            contentData.setTitle(form.getTitle());
            contentData.setSlug(slugify.slugify(form.getSlug()));
            contentData.setComputedSlug(getComputedSlug(form.getContentType(), form.getSlug(), begin, contentData.getLanguage().getLocale(), applicationService.getApplicationConfig().isForcedLangInUrl()));

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
                else{
                    data.put("image_preview", form.getPreviousFile());
                }
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
            if(!StringUtils.isEmpty(form.getTags())){
                String[] tags = form.getTags().split(",");
                if(tags.length > 0){
                    taxonomyTermEntityList.addAll(taxonomyService.createIfNotExist(Arrays.asList(tags), "TAG"));
                }
            }
            if(!StringUtils.isEmpty(form.getCategories())){
                String[] categories = form.getCategories().split(",");
                if(categories.length > 0){
                    taxonomyTermEntityList.addAll(taxonomyService.createIfNotExist(Arrays.asList(categories), "CATEGORY"));
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
                form.getContentType()+ "/" +
                content.getId() +
                "?langCode=" +
                form.getSelectLanguage();
    }


    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(String contentType, String locale) throws Exception {
        if(!contentService.contentTypeExist(contentType)){
            throw new ResourceNotFoundException();
        }

        return contentService.getContentJsonByTypeAndLocale(contentType, locale);
        //return contentService.("all");
    }

    private String getComputedSlug(String type, String slug, Date dateCreated, String locale, boolean forceLangInUrl) {
        StringBuilder computedSlug = new StringBuilder();
        DateTime dateTime = new DateTime(dateCreated);

        if(forceLangInUrl){
            computedSlug.append("/").append(locale);
        }
        switch (type){
            case "NEWS" :
                computedSlug.append("/news/");
                break;
            case "EVENT" :
                computedSlug.append("/event/");
                break;
            case "ARTICLE" :
                computedSlug.append("/article/");
                break;
            default:
                throw new IllegalArgumentException("Invalid content type : " + type);
        }

        int day = dateTime.getDayOfMonth();
        int month = dateTime.getMonthOfYear();
        int year = dateTime.getYear();

        computedSlug.append(year)
                    .append("/")
                    .append(CmsUtils.twoDigit(month))
                    .append("/")
                    .append(CmsUtils.twoDigit(day))
                    .append("/")
                    .append(slug);

        return computedSlug.toString();
    }

}
