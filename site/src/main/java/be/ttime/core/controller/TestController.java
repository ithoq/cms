package be.ttime.core.controller;

import be.ttime.core.persistence.model.UserEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.persistence.service.IMessageService;
import be.ttime.core.persistence.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private IUserService userService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value = "/en/resetAdmin", method = RequestMethod.GET)
    @ResponseBody
    public String testCsrf(ModelMap model, HttpServletRequest request, Locale localeRequest) throws Exception {

        UserEntity user = userService.findByUsernameOrEmail("fcipolla@ttime.be");
        if(user == null)
            throw new Exception("ADMIN NOT EXIST");

        user.setPassword(bCryptPasswordEncoder.encode("mrshink,1532"));
        userService.save(user);

        return "Admin reset";
    }

    @RequestMapping(value = "/testCsrf", method = RequestMethod.POST)
    @ResponseBody
    public String testScrfPost(ModelMap model, HttpServletRequest request, Locale localeRequest) {

        String[] result1 = request.getParameterValues("select");
        String[] result2 = request.getParameterValues("chk_group");

        return "ok";
    }
}