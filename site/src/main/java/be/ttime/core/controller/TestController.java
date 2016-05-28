package be.ttime.core.controller;

import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IContentService;
import be.ttime.core.persistence.service.IMessageService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Controller
public class TestController {

    @Autowired
    private IContentService pageService;

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private MessageSource messages;

    @RequestMapping(value = "/testCsrf", method = RequestMethod.GET)
    public String testCsrf(ModelMap model, HttpServletRequest request, Locale localeRequest) {


        Gson gson = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String input = "2016-05-26 17:36:48";
        Date d = null;


        try {
            d = formatter.parse(input);
        } catch (ParseException e) {
            d = new Date();
        }

        String result = gson.toJson(d);


        Date resultDate = gson.fromJson(result, Date.class);

        //String test = format.format(d);

        model.put("date", d );

        model.put("result", resultDate );


        return "csrf";
    }

    @RequestMapping(value = "/testCsrf", method = RequestMethod.POST)
    @ResponseBody
    public String testScrfPost(ModelMap model, HttpServletRequest request, Locale localeRequest) {

        String[] result1 = request.getParameterValues("select");
        String[] result2 = request.getParameterValues("chk_group");

        return "ok";
    }
}