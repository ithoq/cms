package be.ttime.core.controller;


import be.ttime.core.persistence.model.PageBlockEntity;
import be.ttime.core.persistence.service.IPageBlockService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private IPageBlockService pageBlockService;

    @Autowired
    private PebbleUtils pebbleUtils;

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String home(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {


        PageBlockEntity master = pageBlockService.findByNameAndBlockType("master", PageBlockEntity.BlockType.System);
        PageBlockEntity login = pageBlockService.findByNameAndBlockType("login", PageBlockEntity.BlockType.System);
        if (master == null || login == null) {
            throw new Exception("Template master and Login must exist");
        }

        model.put("attr", CmsUtils.getAttributes(request));
        model.put("get", CmsUtils.getParameters(request));
        model.put("csrf", CmsUtils.getCsrfInput(request));
        model.put("session", request.getSession());
        model.put("title", "login");
        model.put("main", pebbleUtils.parseBlock(login, model));
        return pebbleUtils.parseBlock(master, model);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

}
