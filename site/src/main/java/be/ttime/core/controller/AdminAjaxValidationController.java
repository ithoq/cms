package be.ttime.core.controller;

import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.unbescape.html.HtmlEscape;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/validation")

@Slf4j
public class AdminAjaxValidationController {


    @Autowired
    private PebbleUtils pebbleUtils;

    @Autowired
    private IApplicationService applicationService;

    @RequestMapping(value = "peeble", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @ResponseBody
    public Map peebleValidation(ModelMap model, HttpServletRequest request, String peebleData) {

        boolean error = false;
        String message = "test";
        try{
            CmsUtils.fillModelMap(model, request, applicationService);
            pebbleUtils.parseString(peebleData, model);
        } catch(Exception e){
            message =  e.getMessage();
            error = true;
        }

        Map result = new HashMap<>();
        if(error){
            result.put("error", HtmlEscape.escapeHtml5(message));
        } else {
            result.put("data", "success");
        }


        return result;

    }


}
