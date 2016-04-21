package be.ttime.core.controller;

import be.ttime.core.error.CmsNotInstalledException;
import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.field.PageData;
import be.ttime.core.persistence.model.PageBlockEntity;
import be.ttime.core.persistence.model.PageContentEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IPageBlockService;
import be.ttime.core.persistence.service.IPageService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@RestController
@Slf4j
public class CmsController {

    @Autowired
    private IPageService pageService;

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IPageBlockService pageBlockService;

    @Autowired
    private PebbleUtils pebbleUtils;

    @RequestMapping(method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String page(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

        final String path = request.getRequestURI();
        PageContentEntity content = pageService.findBySlug(path, locale);
        if (content == null) {
            throw new ResourceNotFoundException();
        }

        if (!StringUtils.isEmpty(content.getData())) {
            Gson gson = new Gson();
            PageData pageData = gson.fromJson(content.getData(), PageData.class);
            model.put("data", pageData.getData());
            model.put("dataArray", pageData.getDataArray());
        }

        PageBlockEntity master = pageBlockService.findByNameAndBlockType("master", PageBlockEntity.BlockType.System);

        model.put("attr", CmsUtils.getAttributes(request));
        model.put("get", CmsUtils.getParameters(request));
        model.put("csrf", CmsUtils.getCsrfInput(request));
        model.put("title", content.getSeoTitle());
        model.put("main", pebbleUtils.parseBlock(content.getPage().getPageTemplate().getPageBlock(), model));
        return pebbleUtils.parseBlock(master, model);
    }
  
    /*
    @RequestMapping(method = RequestMethod.POST)
    public String pagePost(ModelMap model, HttpServletRequest request) throws Exception {

        return "CMS controller POST";
    }
    */
}
