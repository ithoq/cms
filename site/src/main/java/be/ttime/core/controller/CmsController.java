package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.field.PageData;
import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.persistence.service.IContentService;
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
    private IContentService pageService;

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IBlockService blockService;

    @Autowired
    private PebbleUtils pebbleUtils;

    @RequestMapping(method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String page(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

        final String path = request.getRequestURI();
        ContentDataEntity contentData = pageService.findBySlug(path, locale);
        if (contentData == null) {
            throw new ResourceNotFoundException();
        }

        if (!StringUtils.isEmpty(contentData.getData())) {
            Gson gson = new Gson();
            PageData pageData = gson.fromJson(contentData.getData(), PageData.class);
            model.put("data", pageData.getData());
            model.put("dataArray", pageData.getDataArray());
        }

        BlockEntity master = blockService.findByNameAndBlockType(CmsUtils.BLOCK_PAGE_MASTER, CmsUtils.BLOCKTYPE_SYSTEM);


        CmsUtils.fillModelMap(model,request);

        //model.put("title", content.getSeoTitle());
        model.put("main", pebbleUtils.parseBlock(contentData.getContent().getContentTemplate().getBlock(), model));
        return pebbleUtils.parseBlock(master, model);
    }
  
    /*
    @RequestMapping(method = RequestMethod.POST)
    public String pagePost(ModelMap model, HttpServletRequest request) throws Exception {

        return "CMS controller POST";
    }
    */
}
