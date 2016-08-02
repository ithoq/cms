package be.ttime.core.controller;

import be.ttime.core.model.fm.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Slf4j
@RequestMapping(value = "/admin/fileManager")
public class AdminFileManagerController {

    @Autowired
    private ServletContext servletContext;

    private final static String VIEWPATH = "admin/fm/";

    private boolean auth() {
        return true;
    }

    @RequestMapping(value = "index.html", method = RequestMethod.GET)
    public String home(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/api")
    public void fm(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {


        FileManager fm = new FileManager(servletContext, LocaleContextHolder.getLocale());

        fm.handleRequest(request, response);
    }
}

