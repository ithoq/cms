package be.ttime.core.controller;

import be.ttime.core.error.PagePersistenceException;
import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.field.PageData;
import be.ttime.core.model.form.CreatePageForm;
import be.ttime.core.model.form.EditPageForm;
import be.ttime.core.model.form.EditPagePositionForm;
import be.ttime.core.persistence.model.ApplicationLanguageEntity;
import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;
import be.ttime.core.persistence.model.ContentTemplateEntity;
import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import be.ttime.core.persistence.model.ContentTypeEntity;
import be.ttime.core.persistence.model.FieldsetEntity;
import be.ttime.core.persistence.model.InputDataEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.persistence.service.IContentTemplateService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.ControllerUtils;
import com.github.slugify.Slugify;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/cms")
@Slf4j
public class AdminCmsController {

    private final static String VIEWPATH = "admin/cms/";
    @Autowired
    private IContentService contentService;
    @Autowired
    private IContentTemplateService contentTemplateService;
    @Autowired
    private IApplicationService applicationService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model) {
        model.put("templates", contentTemplateService.findAllByTypeLike("PAGE%"));
        return VIEWPATH + "home";
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

        ApplicationLanguageEntity appLanguage = applicationService.getSiteApplicationLanguageMap().get(locale);
        if (appLanguage == null) {
            appLanguage = applicationService.getDefaultSiteApplicationLanguage();
        }

        ContentDataEntity contentData = content.getContentDataList().get(appLanguage.getLocale());

        if (contentData == null) {
            contentData = new ContentDataEntity();
            contentData.setContent(content);
            contentData.setLanguage(appLanguage);
            contentService.saveContentData(contentData);
        }

        if (!StringUtils.isEmpty(contentData.getData())) {
            model.put("data", CmsUtils.parseStringToPageDate(contentData.getData()));
        }

        ContentTemplateEntity template = contentTemplateService.find(content.getContentTemplate().getId());
        model.put("template", template);

        model.put("content", content);
        model.put("contentData", contentData);
        model.put("contentLocale", appLanguage.getLocale());

        return VIEWPATH + "page";
    }

    @RequestMapping(value = "/page/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String deletePage(@PathVariable("id") long urlId, HttpServletResponse response) throws Exception {

        if (urlId == 0) {
            response.setStatus(500);
            return "L'id de la page n'existe pas";
        }

        try {
            ContentDataEntity contentData = contentService.findContentData(urlId);
            ContentEntity content = contentService.findContentAdmin(contentData.getContent().getId());
            int size = content.getContentDataList().size();
            if(size <=1){
                contentService.deleteContent(urlId);
            } else {
                contentService.deleteContentData(urlId);
            }

        } catch (Exception e) {
            response.setStatus(500);
            return "An error occurred, please try later";
        }

        return "delete";
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

            ApplicationLanguageEntity language = applicationService.getDefaultSiteApplicationLanguage();
            String pageTitle = form.getName();
            String slug = slugify(pageTitle);
            ContentDataEntity contentData = new ContentDataEntity();
            contentData.setLanguage(language);
            contentData.setTitle(pageTitle + '_' + language.getLocale());
            //content.setSeoTitle(pageTitle);
            contentData.setSlug("/" + slug);

            ContentEntity parent;
            if (form.getParentId() >= 0) {
                parent = contentService.findContentAndContentData(form.getParentId(), applicationService.getDefaultSiteApplicationLanguage().getLocale());
                if (parent == null) {
                    throw new PagePersistenceException("Create page - parent not exist with id " + form.getParentId());
                }
                //page.setLevel(parent.getLevel() + 1);
                content.setContentParent(parent);
            }

            contentData.setComputedSlug(CmsUtils.computeSlug(content, contentData, language.getLocale()));

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

        ContentEntity content;
        ContentDataEntity contentData;

        Long id = form.getContentId();

        // des erreurs ?
        if (result.hasErrors()) {
            response.setStatus(500);
            return ControllerUtils.getValidationErrorsInUl(result.getFieldErrors());
        } else {

            content = contentService.findContentAdmin(form.getContentId());
            contentData = contentService.findContentData(form.getContentDataId());
            if (content == null || contentData == null) {
                throw new Exception("Page or Content can't by null");
            }

            ApplicationLanguageEntity appLanguage = applicationService.getSiteApplicationLanguageMap().get(contentData.getLanguage().getLocale());
            if (appLanguage == null) {
                appLanguage = applicationService.getDefaultSiteApplicationLanguage();
            }

            // retrieve data
            ContentTemplateEntity template = contentTemplateService.find(content.getContentTemplate().getId());
            Slugify slg = new Slugify();
            PageData pageData = new PageData();

            SimpleDateFormat dateFormatter = new SimpleDateFormat(CmsUtils.DATE_FORMAT);
            for (ContentTemplateFieldsetEntity contentTemplateFieldset : template.getContentTemplateFieldset()) {

                FieldsetEntity fieldset = contentTemplateFieldset.getFieldset();
                Map<String, Object> inputsMap = new HashMap<>();
                for (InputDataEntity inputDataEntity : contentTemplateFieldset.getDataEntities()) {
                    String finalName = contentTemplateFieldset.getNamespace() + "_" + slg.slugify(inputDataEntity.getInputDefinition().getName());
                    String type = inputDataEntity.getInputDefinition().getType();
                    boolean isArray = contentTemplateFieldset.isArray() && fieldset.isArray();
                    if (type.equals("date")) {

                        if (isArray) {
                            String[] stringDateArray = request.getParameter(finalName).split(",");
                            Date[] dateArray = new Date[stringDateArray.length];
                            for (int i = 0; i < stringDateArray.length; i++) {
                                dateArray[i] = dateFormatter.parse(stringDateArray[i]);
                            }
                            pageData.getDataDateArray().put(finalName, dateArray);
                        } else {
                            pageData.getDataDate().put(finalName, dateFormatter.parse(request.getParameter(finalName)));
                        }


                    } else if (type.equals("file")) {
                        // TODO : Upload and save the name
                        /*
                        if(isArray){
                            pageData.getDataStringArray().put(finalName, request.getParameterValues(finalName));
                        } else{
                            pageData.getDataString().put(finalName, request.getParameter(finalName));
                        }*/
                    } else if (type.equals("integer")) {
                        if (isArray) {
                            final String[] stringArray = request.getParameterValues(finalName);
                            final Integer[] ints = new Integer[stringArray.length];
                            for (int i = 0; i < stringArray.length; i++) {
                                ints[i] = Integer.parseInt(stringArray[i]);
                            }
                            pageData.getDataIntegerArray().put(finalName, ints);
                        } else {
                            pageData.getDataInteger().put(finalName, Integer.parseInt(request.getParameter(finalName)));
                        }
                    } else if (type.equals("double")) {
                        if (isArray) {
                            final String[] stringArray = request.getParameterValues(finalName);
                            final Double[] doubles = new Double[stringArray.length];
                            for (int i = 0; i < stringArray.length; i++) {
                                doubles[i] = Double.parseDouble(stringArray[i]);
                            }
                            pageData.getDataDoubleArray().put(finalName, doubles);
                        } else {
                            pageData.getDataDouble().put(finalName, Double.parseDouble(request.getParameter(finalName)));
                        }
                    } else if (type.equals("boolean")) {
                        if (isArray) {
                            final String[] stringArray = request.getParameterValues(finalName);
                            final Boolean[] booleans = new Boolean[stringArray.length];
                            for (int i = 0; i < stringArray.length; i++) {
                                booleans[i] = Boolean.parseBoolean(stringArray[i]);
                            }
                            pageData.getDataBooleanArray().put(finalName, booleans);
                        } else {
                            pageData.getDataString().put(finalName, request.getParameter(finalName));
                        }
                    } else {
                        if (isArray) {
                            pageData.getDataStringArray().put(finalName, request.getParameterValues(finalName));
                        } else {
                            pageData.getDataString().put(finalName, request.getParameter(finalName));
                        }
                    }

                }
            }


            Gson gson = new GsonBuilder().disableHtmlEscaping().setDateFormat(CmsUtils.DATETIME_FORMAT).create();
            String json = gson.toJson(pageData);

            content.setName(form.getName());

            content.setMenuItem(form.isMenuItem());
            content.setEnabled(form.isEnabled());

            contentData.setData(json);
            contentData.setTitle(form.getName());
            contentData.setSlug(form.getSlug());

            contentData.setComputedSlug(CmsUtils.computeSlug(content, contentData, appLanguage.getLocale()));

//            content.setSeoDescription(form.getSeoDescription());
//            content.setSeoH1(form.getSeoH1());
//            content.setSeoTag(form.getSeoTag());
//            content.setSeoTitle(form.getSeoTitle());
            contentService.saveContent(content);
            contentService.saveContentData(contentData);
        }

        return "OK";
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
}
