package be.ttime.core.controller;

import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/admin/webContent")
@Slf4j
public class AdminWebContentController {

    private final static String VIEWPATH = "admin/webContent/";

    @Autowired
    IContentService contentService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model) throws Exception{
        model.put("contentType", "NEWS");
        contentService.getContentJsonByTypeAndLocale(CmsUtils.CONTENT_TYPE_NEWS, "en");
        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(String contentType, String locale) {

        return null;
        //return contentService.("all");
    }
}
