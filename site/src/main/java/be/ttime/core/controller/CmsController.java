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
import com.mitchellbosecke.pebble.spring4.context.Beans;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
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

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String page(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

        final String path = request.getRequestURI();
        //GET DATA WITH FILES,DIC,COMMENTS,...
        ContentEntity content = contentService.findBySlug(path, locale);
        ContentDataEntity contentData = content.getContentDataList().get(locale.toString());
        String templateName =  content.getContentTemplate().getName().toLowerCase();
        if (content == null || !content.isEnabled() || !contentData.isEnabled() || templateName.equals("folder")) {
            throw new ResourceNotFoundException();
        }

        if(content.isMemberOnly()){
            if(!CmsUtils.hasRole("ROLE_MEMBER")) {
                response.sendRedirect("/login");
            }
        }
        /*
        Example
        if(!CmsUtils.hasRoles(contentService.getRoleForContent(content))){

            throw new AccessDeniedException("you don't have the required privileges to perform this action");
        }*/
        model.put("title", contentData.getTitle());
        if (!StringUtils.isEmpty(contentData.getData())) {
            HashMap<String, Object> data = CmsUtils.parseData(contentData.getData());

            if(templateName.equals("link")){
                String URL = (String) data.get("_text");
                response.sendRedirect(URL);
                return null;
            }

            model.put("data", data);
            //model.put("dataArray", pageData.getDataArray());
        }

        CmsUtils.fillModelMap(model,request, applicationService);
        model.put("beans", new Beans(applicationContext));

        model.put("contentData", contentData);
        model.put("content", content);
        // Pas grave pour les perfs car les blocks seront dans le cache
        BlockEntity master = blockService.find(CmsUtils.BLOCK_PAGE_MASTER);
        ContentTemplateEntity templateEntity = contentTemplateService.find(contentData.getContent().getContentTemplate().getId());
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
