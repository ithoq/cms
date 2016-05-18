package be.ttime.core.controller;


import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.service.IBlockService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private IBlockService blockService;

    @Autowired
    private PebbleUtils pebbleUtils;

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String home(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

        BlockEntity master = blockService.findByNameAndBlockType(CmsUtils.BLOCK_PAGE_MASTER, CmsUtils.BLOCKTYPE_SYSTEM);
        BlockEntity login = blockService.findByNameAndBlockType(CmsUtils.BLOCK_PAGE_LOGIN, CmsUtils.BLOCKTYPE_SYSTEM);

        model.put("attr", CmsUtils.getAttributes(request));
        model.put("get", CmsUtils.getParameters(request));
        model.put("csrf", CmsUtils.getCsrfInput(request));
        model.put("session", request.getSession());
        model.put("title", "login");
        model.put("main", pebbleUtils.parseBlock(login, model));
        response.setContentType("text/html");
        return pebbleUtils.parseBlock(master, model);

    }

    @RequestMapping(value = "/loginAdmin", method = RequestMethod.GET)
    public String home(ModelMap model) {
        return "admin/login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public RedirectView logoutPage(HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        RedirectView redirect = new RedirectView("/login?logout");
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

}
