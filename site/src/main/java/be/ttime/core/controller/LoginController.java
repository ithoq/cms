package be.ttime.core.controller;


import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private IBlockService blockService;
    @Autowired
    private IApplicationService applicationService;
    @Autowired
    private PebbleUtils pebbleUtils;


    @RequestMapping(value = "/{lang:[a-z]{2}(?:_[A-Z]{2})?}/login", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String home(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {

        BlockEntity master = blockService.find(CmsUtils.BLOCK_PAGE_MASTER);
        BlockEntity login = blockService.find(CmsUtils.BLOCK_PAGE_LOGIN);

        CmsUtils.fillModelMap(model,request, applicationService);

        model.put("title", "login");
        model.put("main", pebbleUtils.parseBlock(login, model));
        response.setContentType("text/html");
        String result = pebbleUtils.parseBlock(master, model);
        return result;
    }

    @RequestMapping(value = {"/admin", "/admin/"}, method = RequestMethod.GET)
    public String adminHome()
    {
        return "redirect:/admin/home";
    }

    @RequestMapping(value = "/admin/login", method = RequestMethod.GET)
    public String home(ModelMap model, HttpServletRequest request)
    {
        CmsUtils.fillModelMap(model,request, applicationService);
        return "admin/login";
    }

    /*
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public RedirectView logoutPage(HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        String isAdmin = request.getParameter("isAdmin");
        String targetUrl =  isAdmin == null ? "/login?logout" : "/admin/login?logout";

        RedirectView redirect = new RedirectView(targetUrl);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }*/

}
