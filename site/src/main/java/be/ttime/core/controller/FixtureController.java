package be.ttime.core.controller;

import be.ttime.core.persistence.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FixtureController {

    @Autowired
    private IApplicationService applicationService;
    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/install", method = RequestMethod.GET)
    @ResponseBody
    public String dbinsert(ModelMap model, HttpServletRequest request) {

        return "installation";
    }
}
