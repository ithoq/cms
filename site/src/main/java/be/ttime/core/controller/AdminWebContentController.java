package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.form.AdminWebContentForm;
import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.persistence.service.ITaxonomyService;
import be.ttime.core.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model) throws Exception{
        model.put("contentType", "NEWS");

        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/edit/{contentType}/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("contentType") String contentType, @PathVariable("id") Long id, String langCode, ModelMap model) throws Exception{

        if(!contentService.contentTypeExist(contentType)){
            throw new ResourceNotFoundException();
        }
        ContentEntity content = null;
        ContentDataEntity contentData = null;
        if(id != null && id != 0){
            content =  contentService.findContentAdmin(id);
            if(content == null){
                throw new ResourceNotFoundException();
            }
        }

        if(langCode == null){
            langCode = applicationService.getDefaultSiteLang();
        }
        if(content != null){
            contentData = content.getContentDataList().get(langCode);
        }

        model.put("content", content);
        model.put("data", contentData);
        model.put("contentType", contentType);
        model.put("tags", taxonomyService.findByTypeJson("TAG"));
        model.put("categories", taxonomyService.findByTypeJson("CATEGORY"));
        return VIEWPATH + "edit";
    }



    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String save(@Valid AdminWebContentForm form, BindingResult result, MultipartHttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception{

        ContentEntity content = contentService.findContent(7L);
        ContentDataEntity contentData = content.getContentDataList().get("en");
        contentService.saveContentData(contentData);
        return null;
        // if errors
        /*
        if (result.hasErrors()) {


        } else{

            Date begin = CmsUtils.parseDate(form.getDateBegin(), form.getDateTimeBegin());
            Date end =  CmsUtils.parseDate(form.getDateEnd(),form.getDateTimeEnd());
            Map<String, String> data = new HashMap<>();
            if(form.getThumbnail() != null && !form.getThumbnail().isEmpty()){
                File uploadedFile = CmsUtils.uploadFile(form.getThumbnail(), false);
                data.put("image_preview", uploadedFile.getName());
            }
            ContentEntity content = null;
            ContentDataEntity contentData = null;

            // get content
            if(form.getContentId() == null){
                content = new ContentEntity();
            }
            else{
                content = contentService.findContent(form.getContentId());
                content = contentService.findContent(7L);
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
            content.setName(form.getTitle());
            content.setContentType(new ContentTypeEntity(form.getContentType()));
            contentService.saveContent(content);
            //contentService.saveContentData(contentData);

        }*/
       // return edit(form.getContentType(), form.getContentId(), form.getSelectLanguage(), model);
    }


    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(String contentType, String locale) throws Exception {

        return contentService.getContentJsonByTypeAndLocale(CmsUtils.CONTENT_TYPE_NEWS, "en");
        //return contentService.("all");
    }

}
