package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.field.Field;
import be.ttime.core.model.field.Input;
import be.ttime.core.model.field.PageData;
import be.ttime.core.model.form.CreatePageForm;
import be.ttime.core.model.form.EditPageForm;
import be.ttime.core.model.form.EditPagePositionForm;
import be.ttime.core.persistence.dao.PageEntity;
import be.ttime.core.persistence.dao.PageTemplateEntity;
import be.ttime.core.persistence.service.IPageService;
import be.ttime.core.persistence.service.IPageTemplateService;
import be.ttime.core.util.ControllerUtils;
import be.ttime.core.util.PebbleUtils;
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
import org.springframework.validation.ObjectError;
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
    private IPageTemplateService pageTemplateService;
    @Autowired
    private PebbleUtils pebbleUtils;

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
    public String getPage(ModelMap model, @PathVariable("id") Long id) {

        if (id == null)
            throw new ResourceNotFoundException();

        PageEntity page = pageService.findWithChildren(id);
        Gson gson = new Gson();
        if (page == null)
            throw new ResourceNotFoundException();

        if (!StringUtils.isEmpty(page.getData())) {
            try {
                PageData pageData = gson.fromJson(page.getData(), PageData.class);
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
                String slug = slg.slugify(form.getName());
                PageEntity parent;
                if (form.getParentId() == -1) {
                    page.setLevel(0);
                    page.setSlug("/" + slug);
                } else {
                    parent = pageService.find(form.getParentId());
                    if (parent == null) {
                        throw new Exception("Create page - parent not exist with id " + form.getParentId());
                    }
                    page.setLevel(parent.getLevel() + 1);
                    page.setPageParent(parent);
                    page.setSlug(parent.getSlug() + '/' + slug);
                }

                if (form.getTemplateId() != -1) {
                    PageTemplateEntity template = new PageTemplateEntity();
                    template.setId(form.getTemplateId());
                    page.setPageTemplate(template);
                }
                pageService.save(page);
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
        Long id = form.getId();
        if (!Objects.equals(urlId, form.getId())) {
            result.addError(new ObjectError("id", "error in the id"));
        }

        // des erreurs ?
        if (result.hasErrors()) {
            response.setStatus(500);
            return ControllerUtils.getValidationErrorsInUl(result.getFieldErrors());
        } else {

            page = pageService.find(id);
            if (page == null) {
                throw new Exception();
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

            page.setData(json);

            page.setModifiedDate(new Date());
            page.setName(form.getName());
            page.setSlug(form.getSlug());
            page.setMenuItem(form.isMenuItem());
            page.setEnabled(form.isEnabled());
            page.setSeoDescription(form.getSeoDescription());
            page.setSeoH1(form.getSeoH1());
            page.setSeoTag(form.getSeoTag());
            page.setSeoTitle(form.getSeoTitle());
            pageService.save(page);
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

        pageService.save(pages);
        return "Ok";
    }
}