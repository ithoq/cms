package be.ttime.controller;

import be.ttime.core.persistence.dao.LocalizedMessageEntity;
import be.ttime.core.persistence.service.ILocalizedMessageService;
import be.ttime.core.persistence.service.IPageBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@RestController
public class TestController {

    @Autowired
    private IPageBlockService pageBlockRepository;

    @Autowired
    private ILocalizedMessageService localizedMessageService;

    @Autowired
    private MessageSource messageSource;


    @RequestMapping(value = "/testHibernateSecondCache", method = RequestMethod.GET)
    @ResponseBody
    public String cache(ModelMap model, HttpServletRequest request, Locale locale) {

        List<LocalizedMessageEntity> messages = localizedMessageService.findAll();

        List<LocalizedMessageEntity> messages2 = localizedMessageService.findAll();

        List<LocalizedMessageEntity> messages3 = localizedMessageService.findAll();

        return "ok";
    }
}