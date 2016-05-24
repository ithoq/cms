package be.ttime.core.controller;

import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IMessageService;
import be.ttime.core.persistence.service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
public class TestController {

    @Autowired
    private IContentService pageService;

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private MessageSource messages;

    @RequestMapping(value = "/testCsrf", method = RequestMethod.GET)
    public String testCsrf(ModelMap model, HttpServletRequest request, Locale localeRequest) {

        return "csrf";
    }

    @RequestMapping(value = "/testCsrf", method = RequestMethod.POST)
    @ResponseBody
    public String testScrfPost(ModelMap model, HttpServletRequest request, Locale localeRequest) {

        return "ok";
    }
}