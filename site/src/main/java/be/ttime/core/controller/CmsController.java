package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;
import be.ttime.core.persistence.model.ContentTemplateEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.persistence.service.IContentTemplateService;
import be.ttime.core.service.ApplicationMailer;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private IContentService contentService;

    @Autowired
    private IContentTemplateService contentTemplateService;

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IBlockService blockService;

    @Autowired
    private PebbleUtils pebbleUtils;

    @Autowired
    @Qualifier("mailService")
    private ApplicationMailer applicationMailer;

    @RequestMapping(method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String page(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

        final String path = request.getRequestURI();
        //GET DATA WITH FILES,DIC,COMMENTS,...
        ContentDataEntity contentData = contentService.findBySlug(path, locale);

        ContentEntity contentParent = contentData.getContent();

        if (contentData == null || !contentData.isEnabled()) {
            throw new ResourceNotFoundException();
        }

        // TODO: Vérifier les droits

        model.put("title", contentData.getTitle());
        if (!StringUtils.isEmpty(contentData.getData())) {
            HashMap<String, Object> data = CmsUtils.parseData(contentData.getData());
            model.put("data", data);
            //model.put("dataArray", pageData.getDataArray());
        }

        CmsUtils.fillModelMap(model,request);
        model.put("contentData", contentData);
        model.put("content", contentParent);
        // Pas grave pour les perfs car les blocks seront dans le cache
        BlockEntity master = blockService.find(CmsUtils.BLOCK_PAGE_MASTER);
        ContentTemplateEntity templateEntity = contentTemplateService.find(contentData.getContent().getContentTemplate().getId());
        BlockEntity content = blockService.find(templateEntity.getBlock().getName());

        model.put("main",  pebbleUtils.parseBlock(content, model));

        StringBuilder include_top = new StringBuilder();
        StringBuilder include_bottom = new StringBuilder();

        if(!StringUtils.isEmpty(templateEntity.getIncludeTop())){
            include_top.append(templateEntity.getIncludeTop());
        }
        if(!StringUtils.isEmpty(contentParent.getIncludeTop())){
            include_top.append('\n').append(contentParent.getIncludeTop());
        }

        if(!StringUtils.isEmpty(templateEntity.getIncludeBottom())){
            include_bottom.append(templateEntity.getIncludeBottom());
        }
        if(!StringUtils.isEmpty(contentParent.getIncludeBottom())){
            include_bottom.append('\n').append(contentParent.getIncludeBottom());
        }

        //PageableResult<ContentEntity> result = contentService.findWebContent(locale.toString(), null, null, null, null , "NEWS", 1, 1L, 0L);

        //applicationMailer.sendMail("fcipolla@ttime.be", "Test d'application email", "Ceci est le body de mon email!");

        model.put("include_top", include_top.toString() );
        model.put("include_bottom", include_bottom.toString());

        return pebbleUtils.parseBlock(master, model);
    }
  
    /*
        @RequestMapping(method = RequestMethod.POST)
        public String pagePost(ModelMap model, HttpServletRequest request) throws Exception {

            return "CMS controller POST";
        }
    */
}
