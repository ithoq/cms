package be.ttime.core.controller;

import be.ttime.core.service.IMailer;
import be.ttime.core.service.IRecaptchaService;
import be.ttime.core.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class FormController {

    @Autowired
    private IMailer mailer;
    @Autowired
    private IRecaptchaService recaptchaService;
    @Value("${email.contact}")
    private String emailContact;

    @RequestMapping(value = "/contact", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String contactForm(HttpServletRequest request, HttpServletResponse response) throws Exception{

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message =  request.getParameter("message");
        String ip = CmsUtils.getRemoteIp(request);
        String captchaResponse = request.getParameter("g-recaptcha-response");
        boolean captchaResult = recaptchaService.isResponseValid(null, captchaResponse);
        boolean error = false;

        if(true){
            throw new Exception("test exception");
        }

        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(email) || !captchaResult ){
            error= true;
        }

        if(!error){
            StringBuilder builder = new StringBuilder();
            builder.append("<b>Name</b> : " + name + "<br>");
            builder.append("<b>Email</b> : " + email + "<br>");
            builder.append("<b>Message</b> : " + message + "<br>");
            try {
                mailer.sendMail(emailContact, "Website - contact", builder.toString());
            } catch (MessagingException e) {
                error= true;
                log.error("Error contact form", e);
            } catch (UnsupportedEncodingException e) {
                error= true;
                log.error("Error contact form", e);
            }
        }


        return !error ? "redirect:/contact?success" : "redirect:/contact?error";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST, produces="application/json")
    @ResponseBody
    public String handleForm(HttpServletRequest request, HttpServletResponse response){

        JSONObject resp = new JSONObject();
        List<Map<String, String>> errors = new ArrayList<>();

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message =  request.getParameter("message");
        String ip = CmsUtils.getRemoteIp(request);
        String captchaResponse = request.getParameter("g-recaptcha-response");
        boolean captchaResult = recaptchaService.isResponseValid(null, captchaResponse);

        if(StringUtils.isEmpty(name)){
            Map<String,String> nameError = new HashMap<>();
            nameError.put("source", "nom");
            nameError.put("title", "erreur de validation");
            nameError.put("detail", "le nom est requis");
            errors.add(nameError);
        }
        if(StringUtils.isEmpty(email)){
            Map<String,String> emailError = new HashMap<>();
            emailError.put("source", "email");
            emailError.put("title", "erreur de validation");
            emailError.put("detail", "l'email est requis");
            errors.add(emailError);
        }
        if(!captchaResult){
            Map<String,String> captchaError = new HashMap<>();
            captchaError.put("source", "captcha");
            captchaError.put("title", "erreur de validation");
            captchaError.put("detail", "le captcha est incorrecte");
            errors.add(captchaError);
        }

        if(errors.isEmpty()){
            resp.put("data", "");
        } else{
            resp.put("errors", errors);
        }

        return resp.toString();
    }
}
