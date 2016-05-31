package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
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
        //GET DATA WITH FILES,DIC,COMMENTS,...
        ContentDataEntity contentData = pageService.findBySlug(path, locale);

        if (contentData == null) {
            throw new ResourceNotFoundException();
        }

        // TODO: Vérifier les droits


         if (!StringUtils.isEmpty(contentData.getData())) {
            HashMap<String, Object> data = CmsUtils.parseData(contentData.getData());
            model.put("data", data);
            //model.put("dataArray", pageData.getDataArray());
        }

        CmsUtils.fillModelMap(model,request);
        model.put("content", contentData);
        // Pas grave pour les perfs car les blocks seront dans le cache
        BlockEntity master = blockService.findByNameAndBlockType(CmsUtils.BLOCK_PAGE_MASTER, CmsUtils.BLOCK_TYPE_SYSTEM);
        String blockName = contentData.getContent().getContentTemplate().getBlock().getName();
        BlockEntity content = blockService.findByNameAndBlockType(contentData.getContent().getContentTemplate().getBlock().getName(),CmsUtils.BLOCK_TYPE_CONTENT_TEMPLATE);
        model.put("main",  pebbleUtils.parseBlock(content, model));
        return pebbleUtils.parseBlock(master, model);
    }
  
    /*

    @RequestMapping(method = RequestMethod.POST)
    public String pagePost(ModelMap model, HttpServletRequest request) throws Exception {

        return "CMS controller POST";
    }
    */
}
