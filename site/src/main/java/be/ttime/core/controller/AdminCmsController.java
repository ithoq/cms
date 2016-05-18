package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.field.Field;
import be.ttime.core.model.field.Input;
import be.ttime.core.model.field.PageData;
import be.ttime.core.model.form.CreatePageForm;
import be.ttime.core.model.form.EditPageForm;
import be.ttime.core.model.form.EditPagePositionForm;
import be.ttime.core.persistence.model.ApplicationLanguageEntity;
import be.ttime.core.persistence.model.ContentEntity;
import be.ttime.core.persistence.model.ContentTemplateEntity;
import be.ttime.core.persistence.model.PageEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IPageService;
import be.ttime.core.persistence.service.IContentTemplateService;
import be.ttime.core.util.ControllerUtils;
import com.github.slugify.Slugify;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import java.lang.reflect.Type;
import java.util.*;

@Controller
@RequestMapping(value = "/admin/cms")
@Slf4j
public class AdminCmsController {

    private final static String VIEWPATH = "admin/cms/";
    @Autowired
    private IPageService pageService;
    @Autowired
    private IContentTemplateService pageTemplateService;
    @Autowired
    private IApplicationService applicationService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model) {
        model.put("templates", pageTemplateService.findAll());
        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getTree() {
        return pageService.getPagesTree();
    }

    @RequestMapping(value = "/page/{id}", method = RequestMethod.GET)
    public String getPage(ModelMap model, @PathVariable("id") Long id, String locale) throws Exception {

        if (id == null)
            throw new ResourceNotFoundException();

        PageEntity page = pageService.findWithChildren(id);

        ApplicationLanguageEntity appLanguage = applicationService.getSiteApplicationLanguageMap().get(locale);
        if(appLanguage == null){
            appLanguage = applicationService.getDefaultSiteApplicationLanguage();
        }

        ContentEntity content = null;

        for (ContentEntity c : page.getPageContents()) {
            if (c.getLanguage().getLocale().equals(appLanguage.getLocale())) {
                content = c;
            }
        }

        if (content == null) {
            content = new ContentEntity();
            content.setCreatedDate(new Date());
            content.setPage(page);
            content.setLanguage(appLanguage);
            pageService.saveContent(content);
        }

        Gson gson = new Gson();
        if (page == null)
            throw new ResourceNotFoundException();

        if (!StringUtils.isEmpty(content.getData())) {
            try {
                PageData pageData = gson.fromJson(content.getData(), PageData.class);
                model.put("pageData", pageData);
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        try {
            Type listType = new TypeToken<ArrayList<Field>>() {
            }.getType();
            List<Field> fields = gson.fromJson(page.getPageTemplate().getFields(), listType);
            model.put("fields", fields);
        } catch (Exception e) {
            log.error(e.toString());
        }

        model.put("page", page);
        model.put("content", content);
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
            pageService.delete(urlId);
        } catch (Exception e) {
            response.setStatus(500);
            return "An error occurred, please try later";
        }

        return "delete";
    }


    @RequestMapping(value = "/page/create", method = RequestMethod.POST)
    @ResponseBody
    public String createPage(@Valid CreatePageForm form, BindingResult result, HttpServletResponse response) {

        if (result.hasErrors()) {
            response.setStatus(500);
            return ControllerUtils.getValidationErrorsInUl(result.getFieldErrors());
        } else {
            try {
                Slugify slg = new Slugify();

                PageEntity page = new PageEntity();
                page.setName(form.getName());
                page.setCreatedDate(new Date());

                Locale defLocale = applicationService.getDefaultSiteLocale();
                String lang = applicationService.getDefaultSiteLang();
                String pageTitle = form.getName() + '-' + lang;
                String slug = slg.slugify(pageTitle);
                ContentEntity content = new ContentEntity();
                content.setLanguage(applicationService.getDefaultSiteApplicationLanguage());
                content.setSeoTitle(pageTitle);
                content.setSlug("/" + slug);
                PageEntity parent;
                if (form.getParentId() == -1) {
                    page.setLevel(0);
                    content.setComputedSlug(content.getSlug());
                } else {
                    parent = pageService.find(form.getParentId());
                    if (parent == null) {
                        throw new Exception("Create page - parent not exist with id " + form.getParentId());
                    }
                    page.setLevel(parent.getLevel() + 1);
                    page.setPageParent(parent);

                    List<ContentEntity> contents = page.getPageContents();
                    ContentEntity parentContent = null;
                    for (ContentEntity c : contents) {
                        if (c.getLanguage() == applicationService.getDefaultSiteApplicationLanguage()) {
                            parentContent = c;
                        }
                    }

                    if (parentContent == null)
                        throw new Exception("Parent with id : " + parent.getId() + " don't have a default content");

                    content.setComputedSlug(parentContent.getComputedSlug() + '/' + content.getSlug());
                }

                if (form.getTemplateId() != -1) {
                    ContentTemplateEntity template = new ContentTemplateEntity();
                    template.setId(form.getTemplateId());
                    page.setPageTemplate(template);
                }
                pageService.savePage(page);
            } catch (Exception e) {
                // Logger
                response.setStatus(500);
                return "Erreur du serveur - veuillez ressayer plus tard";
            }
        }
        return "OK";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String postPage(@PathVariable("id") Long urlId, @Valid EditPageForm form, BindingResult result, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PageEntity page;
        ContentEntity content;

        Long id = form.getPageId();

        // des erreurs ?
        if (result.hasErrors()) {
            response.setStatus(500);
            return ControllerUtils.getValidationErrorsInUl(result.getFieldErrors());
        } else {

            page = pageService.find(form.getPageId());
            content = pageService.findContentById(form.getContentId());
            if (page == null || content == null) {
                throw new Exception("Page or Content can't by null");
            }

            ApplicationLanguageEntity appLanguage = applicationService.getSiteApplicationLanguageMap().get(content.getLanguage().getLocale());
            if (appLanguage == null) {
                appLanguage = applicationService.getDefaultSiteApplicationLanguage();
            }


            Type listType = new TypeToken<ArrayList<Field>>() {
            }.getType();
            List<Field> fields = new Gson().fromJson(page.getPageTemplate().getFields(), listType);

            PageData pageData = new PageData();
            String inputName;
            for (Field field : fields) {
                for (Input input : field.getInputs()) {
                    inputName = field.getNamespace() + "_" + input.getName();
                    if (input.getType().equals("array")) {
                        pageData.getDataArray().put(inputName, Arrays.asList(request.getParameterValues(inputName)));
                    } else {
                        pageData.getData().put(inputName, request.getParameter(inputName));
                    }
                }
            }

            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            String json = gson.toJson(pageData);


            page.setModifiedDate(new Date());
            page.setName(form.getName());

            page.setMenuItem(form.isMenuItem());
            page.setEnabled(form.isEnabled());

            content.setData(json);
            content.setSlug(form.getSlug());
            content.setSeoDescription(form.getSeoDescription());
            content.setSeoH1(form.getSeoH1());
            content.setSeoTag(form.getSeoTag());
            content.setSeoTitle(form.getSeoTitle());
            content.setModifiedDate(new Date());
            pageService.savePage(page);
            pageService.saveContent(content);
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

        List<PageEntity> pages = new ArrayList<>();
        PageEntity pageParent;
        if (form.getParentId() == null) {
            pageParent = null;
        } else {
            pageParent = new PageEntity();
            pageParent.setId(form.getParentId());
        }

        for (int i = 0; i < idLength; i++) {

            PageEntity p = pageService.find(form.getId()[i]);
            if (p == null) {
                response.setStatus(500);
                return "ID : " + form.getId()[i] + " don't exist";
            }
            p.setPageParent(pageParent);
            p.setLevel(form.getLevel()[i]);
            p.setOrder(form.getPosition()[i]);
            pages.add(p);
        }

        pageService.savePage(pages);
        return "Ok";
    }
}
