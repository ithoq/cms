package be.ttime.core.controller;

import be.ttime.core.error.PagePersistenceException;
import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.field.PageData;
import be.ttime.core.model.form.CreatePageForm;
import be.ttime.core.model.form.EditPageForm;
import be.ttime.core.model.form.EditPagePositionForm;
import be.ttime.core.persistence.model.*;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.persistence.service.IContentTemplateService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.ControllerUtils;
import be.ttime.core.util.PebbleUtils;
import com.github.slugify.Slugify;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mitchellbosecke.pebble.error.PebbleException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/cms")
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN_CMS')")
public class AdminCmsController {

    private final static String VIEWPATH = "admin/cms/";
    @Autowired
    private IContentService contentService;
    @Autowired
    private IContentTemplateService contentTemplateService;
    @Autowired
    private IApplicationService applicationService;
    @Autowired
    private IBlockService blockService;

    @Autowired
    private PebbleUtils pebbleUtils;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model) {
        model.put("templates", contentTemplateService.findAllByTypeLike("PAGE%"));
        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/modalCreate", method = RequestMethod.GET)
    public String getTree(ModelMap model, Long contentParentId) {
        model.put("templates", contentTemplateService.findAllByTypeLike("PAGE%"));
        model.put("contentParentId", contentParentId);

        if(contentParentId != null) model.put("content", contentService.findContentAdmin(contentParentId));
        return VIEWPATH + "modalCreate";
    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getTree() {
        return contentService.getPagesTree();
    }


    @RequestMapping(value = "/page/{id}", method = RequestMethod.GET)
    public String getPage(ModelMap model, @PathVariable("id") Long id, String locale) throws Exception {

        if (id == null)
            throw new ResourceNotFoundException();

        ContentEntity content = contentService.findContentAdmin(id);
        /*ContentEntity contentParent = null;
        if(content.getContentParent() != null){
            contentParent = contentService.findContentAdmin(content.getContentParent().getId());
        }*/
        ContentDataEntity contentData = null;

        ApplicationLanguageEntity appLanguage = null;

        // if we try to display a lang
        if (!StringUtils.isEmpty(locale)) {
            appLanguage = applicationService.getSiteApplicationLanguageMap().get(locale);
            if (appLanguage == null) {
                log.debug("incorrect language : " + locale + " for the content with id : " + content.getId());
                appLanguage = applicationService.getDefaultSiteApplicationLanguage();
            }

            contentData = content.getContentDataList().get(appLanguage.getLocale());
            if(contentData == null) {
                contentData = new ContentDataEntity();
                contentData.setContent(content);
                contentData.setLanguage(appLanguage);
                contentData.setTitle(content.getName());

                Slugify slugify = new Slugify();
                contentData.setSlug(slugify.slugify(content.getName()));
                contentData = contentService.saveContentData(contentData);


                // reload the content
                content = contentService.findContentAdmin(id);
            }
        } else {
            appLanguage = applicationService.getDefaultSiteApplicationLanguage();
            contentData = content.getContentDataList().get(appLanguage.getLocale());
            if(contentData == null) {
                Map.Entry<String, ContentDataEntity> entry = content.getContentDataList().entrySet().iterator().next();
                contentData = entry.getValue();
                appLanguage = applicationService.getSiteApplicationLanguageMap().get(entry.getKey());
            }
        }

        if (!StringUtils.isEmpty(contentData.getData())) {
            model.put("data", CmsUtils.parseStringToPageDate(contentData.getData()));
        }

        ContentTemplateEntity template = contentTemplateService.find(content.getContentTemplate().getId());
        model.put("template", template);
        model.put("allTemplates", contentTemplateService.findAllByTypeLike("PAGE%"));
        model.put("content", content);
        model.put("contentData", contentData);
        model.put("contentLocale", appLanguage.getLocale());

        return VIEWPATH + "page";
    }

    @RequestMapping(value = "/page/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String deletePage(@PathVariable("id") long urlId, HttpServletResponse response) throws Exception {

        String result = "";
        if (urlId == 0) {
            response.setStatus(500);
            return "L'id de la page n'existe pas";
        }

        try {
            ContentDataEntity contentData = contentService.findContentData(urlId);
            ContentEntity content = contentService.findContentAdmin(contentData.getContent().getId());
            int size = content.getContentDataList().size();
            if(size <=1){
                // delete the content
                contentService.deleteContent(content.getId());
                result = "empty";
            } else {
                // delete the content data
                contentService.deleteContentData(urlId);
            }

        } catch (Exception e) {
            response.setStatus(500);
            return "An error occurred, please try later";
        }

        return result;
    }

    private static String slugify(final String input) throws PagePersistenceException {
        try {
            return new Slugify(true).slugify(input);
        } catch (final IOException e) {
            throw new PagePersistenceException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/page/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createPage(final @Valid CreatePageForm form, final BindingResult result) {

        if (result.hasErrors()) {
            final HttpHeaders headers = new HttpHeaders();
            headers.set(CmsUtils.HEADER_VALIDATION_FAILED, Boolean.TRUE.toString());
            return new ResponseEntity<>(ControllerUtils.getValidationErrorsInUl(result.getFieldErrors()), headers, HttpStatus.OK);
        } else {
            ContentEntity content = new ContentEntity();
            content.setName(form.getName());

            ApplicationLanguageEntity language = applicationService.getSiteApplicationLanguageMap().get(form.getPageLangId());
            String pageTitle = form.getName();
            String slug = slugify(pageTitle);
            ContentDataEntity contentData = new ContentDataEntity();
            contentData.setLanguage(language);
            contentData.setTitle(pageTitle + '_' + language.getLocale());
            contentData.setSlug("/" + slug);
            contentData.setEnabled(true);

            ContentEntity parent;
            if (form.getParentId() >= 0) {
                parent = contentService.findContentAndContentData(form.getParentId(), form.getPageLangId());
                if (parent == null) {
                    throw new PagePersistenceException("Create page - parent not exist with id " + form.getParentId());
                }
                //page.setLevel(parent.getLevel() + 1);
                content.setContentParent(parent);
            }

            contentData.setComputedSlug(CmsUtils.computeSlug(content, contentData, language.getLocale(), applicationService.getApplicationConfig().isForcedLangInUrl()));

            ContentTemplateEntity contentTemplateEntity = contentTemplateService.find(form.getTemplateId());
            if (contentTemplateEntity == null) {
                throw new PagePersistenceException("content template don't exist!");
            }

            content.setContentTemplate(contentTemplateEntity);
            content.addContentData(contentData);
            content.setContentType(new ContentTypeEntity(contentTemplateEntity.getContentType().getName()));

            contentService.saveContent(content);
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String postPage(@PathVariable("id") Long urlId, @Valid EditPageForm form, BindingResult result, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Long id = form.getContentId();
        response.setStatus(500);
        // des erreurs ?
        if (result.hasErrors()) {
            return ControllerUtils.getValidationErrorsInUl(result.getFieldErrors());
        } else {
            ContentEntity content = contentService.findContentAdmin(form.getContentId());
            ContentDataEntity contentData = contentService.findContentData(form.getContentDataId());
            fillPageForm(form, request, content, contentData);

            contentService.saveContent(content);
            contentService.saveContentData(contentData);
            response.setStatus(200);
        }

        return "OK";
    }

    @RequestMapping(value = "/preview", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String preview(@Valid EditPageForm form, BindingResult result, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, PebbleException {

        Long id = form.getContentId();
        response.setStatus(500);
        // des erreurs ?
        if (result.hasErrors()) {
            return ControllerUtils.getValidationErrorsInUl(result.getFieldErrors());
        } else {

            ContentEntity content = contentService.findContentAdmin(form.getContentId());
            ContentDataEntity contentData = contentService.findContentData(form.getContentDataId());
            fillPageForm(form, request, content, contentData);

            model.put("title", contentData.getTitle());
            if (!org.springframework.util.StringUtils.isEmpty(contentData.getData())) {
                HashMap<String, Object> data = CmsUtils.parseData(contentData.getData());
                model.put("data", data);
            }

            CmsUtils.fillModelMap(model,request, applicationService);
            model.put("contentData", contentData);
            model.put("content", content);
            // Pas grave pour les perfs car les blocks seront dans le cache
            BlockEntity master = blockService.find(CmsUtils.BLOCK_PAGE_MASTER);
            ContentTemplateEntity templateEntity = contentTemplateService.find(content.getContentTemplate().getId());
            BlockEntity blockTemplate = blockService.find(templateEntity.getBlock().getName());

            model.put("main",  pebbleUtils.parseBlock(blockTemplate, model));

            StringBuilder include_top = new StringBuilder();
            StringBuilder include_bottom = new StringBuilder();

            if(!StringUtils.isEmpty(templateEntity.getIncludeTop())){
                include_top.append(templateEntity.getIncludeTop());
            }
            if(!StringUtils.isEmpty(content.getIncludeTop())){
                include_top.append('\n').append(content.getIncludeTop());
            }

            if(!StringUtils.isEmpty(templateEntity.getIncludeBottom())){
                include_bottom.append(templateEntity.getIncludeBottom());
            }
            if(!StringUtils.isEmpty(content.getIncludeBottom())){
                include_bottom.append('\n').append(content.getIncludeBottom());
            }

            model.put("include_top", include_top.toString() );
            model.put("include_bottom", include_bottom.toString());

            return pebbleUtils.parseBlock(master, model);
        }
    }

    @RequestMapping(value = "/page/updatePosition", method = RequestMethod.POST)
    @ResponseBody
    public String updatePagePosition(EditPagePositionForm form, HttpServletResponse response) throws Exception {

        // I should create annotation
        int idLength = form.getId().length;
        int levelLength = form.getLevel().length;
        int positionLength = form.getPosition().length;

        if (idLength == 0 || idLength != levelLength || levelLength != positionLength) {
            response.setStatus(500);
            return "Array are null or have not the same length";
        }

        List<ContentEntity> pages = new ArrayList<>();
        ContentEntity contentParent;
        if (form.getParentId() == null) {
            contentParent = null;
        } else {
            contentParent = new ContentEntity();
            contentParent.setId(form.getParentId());
        }

        for (int i = 0; i < idLength; i++) {

            ContentEntity p = contentService.findContent(form.getId()[i]);
            if (p == null) {
                response.setStatus(500);
                return "ID : " + form.getId()[i] + " don't exist";
            }
            p.setContentParent(contentParent);
            //p.setLevel(form.getLevel()[i]);
            p.setOrder(form.getPosition()[i]);
            pages.add(p);
        }

        contentService.saveContent(pages);
        return "Ok";
    }

    private void fillPageForm(EditPageForm form, HttpServletRequest request, ContentEntity content, ContentDataEntity contentData ) throws IOException, ParseException {

        // retrieve data
        ContentTemplateEntity template = contentTemplateService.find(form.getTemplateId());
        content.setContentTemplate(template);

        if (content == null || contentData == null || template == null) {
            throw new IllegalArgumentException("The page, the content or the template is incorrect!");
        }

        ApplicationLanguageEntity appLanguage = applicationService.getSiteApplicationLanguageMap().get(contentData.getLanguage().getLocale());
        if (appLanguage == null) {
            appLanguage = applicationService.getDefaultSiteApplicationLanguage();
        }

        PageData pageData = CmsUtils.fillData(template.getContentTemplateFieldset(), request);

        // Form data
        Map<String, String> data = new HashMap<>();


        content.setIncludeTop(form.getDevIncludeTop());
        content.setIncludeBottom(form.getDevIncludeBot());

        data.put("seo_h1", form.getSeoH1());
        data.put("seo_description", form.getSeoDescription());
        data.put("seo_tags", form.getSeoTag());
        pageData.getDataString().putAll(data);

        content.setName(form.getName());

        content.setMenuItem(form.isMenuItem());
        content.setEnabled(form.isEnabled());

        Gson gson = new GsonBuilder().disableHtmlEscaping().setDateFormat(CmsUtils.DATETIME_FORMAT).create();
        contentData.setData(gson.toJson(pageData));

        contentData.setTitle(form.getPageDataTitle());
        contentData.setSlug(form.getSlug());
        contentData.setEnabled(form.isContentDataEnabled());
        contentData.setComputedSlug(CmsUtils.computeSlug(content, contentData, appLanguage.getLocale(), applicationService.getApplicationConfig().isForcedLangInUrl()));

    }
}
