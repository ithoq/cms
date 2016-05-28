package be.ttime.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@Slf4j
@RequestMapping(value = "/admin")
public class AdminHomeController {

    private final static String VIEWPATH = "admin/";

    @RequestMapping(method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String home(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

        return VIEWPATH + "home";
    }
}
