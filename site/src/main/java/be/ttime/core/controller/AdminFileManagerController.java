package be.ttime.core.controller;

import be.fabriceci.fmc.IFileManager;
import be.fabriceci.fmc.error.FileManagerException;
import be.fabriceci.fmc.impl.FileManager;
import be.ttime.core.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasRole('ROLE_ADMIN_FILE_MANAGER')")
public class AdminFileManagerController {

    @Autowired
    private ServletContext servletContext;

    private final static String VIEWPATH = "admin/fm/";

    private boolean auth() {
        return true;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return VIEWPATH + "embded";
    }

    @RequestMapping(value = "index.html", method = RequestMethod.GET)
    public String index(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/api")
    public void fm(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException, FileManagerException {

        IFileManager fm = new FileManager(servletContext, LocaleContextHolder.getLocale(), request, CmsUtils.isSuperAdmin());

        fm.handleRequest(request, response);
    }
}

