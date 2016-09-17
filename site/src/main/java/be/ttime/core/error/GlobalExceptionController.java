package be.ttime.core.error;

import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import com.mitchellbosecke.pebble.spring4.context.Beans;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * General error handler for the application.
 * see doc = https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
 */
@ControllerAdvice // => will make it a global exception handler (not individual)
@Slf4j
class GlobalExceptionController {

    public final static String VIEW_GENERAL = "error/general";
    private final static String VIEW_404 = "error/notFound";

    @Autowired
    private IBlockService blockService;
    @Autowired
    private IApplicationService applicationService;
    @Autowired
    private PebbleUtils pebbleUtils;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ServletContext context;
    @Autowired
    private ApplicationContext applicationContext;

    private  String handleException(final Exception e, HttpServletRequest request, final HttpServletResponse response, int sc) {

        if(sc != 404){
            log.error(request.getRequestURI() + " - " + e.getMessage(), e);
        } else{
            log.warn("404 ERROR : " + request.getRequestURI());
        }

        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setStatus(sc);

        Locale locale = LocaleContextHolder.getLocale();
        ModelMap model = new ModelMap();
        String errorTitleMessage= null;
        String errorPublicBlock = null;
        String result = null;

        if(sc == 404){
            errorTitleMessage = "error.404";
            errorPublicBlock = applicationService.getApplicationConfig().getErrorBlock404();
        } else if(sc == 403) {
            errorTitleMessage = "access denied";
            errorPublicBlock = applicationService.getApplicationConfig().getErrorBlock403();
        } else  if(sc == 503) {
            errorTitleMessage = "access denied";
            errorPublicBlock = applicationService.getApplicationConfig().getErrorBlock503();
        }
        else {
            errorTitleMessage = "error.general";
            errorPublicBlock = applicationService.getApplicationConfig().getErrorBlockGeneral();
        }

        model.put("title", messageSource.getMessage(errorTitleMessage, null, locale));
        model.put("errorCode", sc);
        model.put("errorMsg", e.getMessage());
        model.put("errorStacktrace", e.getStackTrace());
        model.put("beans", new Beans(applicationContext));

        CmsUtils.fillModelMap(model,request, applicationService);

        if(CmsUtils.uriIsAdmin(request)){

            String template;
            InputStream resourceContent = context.getResourceAsStream("/WEB-INF/templates/admin/error/general.peb");
            try {
                template = IOUtils.toString(resourceContent, "UTF-8");
                if(resourceContent != null) {
                    resourceContent.close();
                }
                result = pebbleUtils.parseString(template, model);

            } catch (Exception e2){
                log.error("error during 'error.peb' loading", e2);
            }

        } else {
            BlockEntity master = blockService.find(CmsUtils.BLOCK_PAGE_MASTER);
            BlockEntity login = blockService.find(errorPublicBlock);

            try {
                model.put("main", pebbleUtils.parseBlock(login, model));
                result = pebbleUtils.parseBlock(master, model);
            } catch(Exception e2){
                log.error("Exception in the error controller", e2);
            }
        }
        return result;
    }

    @ExceptionHandler({CmsNotInstalledException.class})
    public String notInstalled() {
        return "redirect:/admin/install";
    }

    @ExceptionHandler({CmsInMaintenanceException.class})
    @ResponseBody
    public String maintenance(HttpServletRequest request, HttpServletResponse response, CmsInMaintenanceException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseBody
    public String notFound(HttpServletRequest request, HttpServletResponse response, ResourceNotFoundException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_NOT_FOUND);
    }

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseBody
    public String notFound(HttpServletRequest request, HttpServletResponse response, UserNotFoundException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_NOT_FOUND);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    @ResponseBody
    public String userExist(HttpServletRequest request, HttpServletResponse response, UserAlreadyExistException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_CONFLICT);
    }

    @ExceptionHandler({InvalidOldPasswordException.class})
    @ResponseBody
    public String invalidPassword(HttpServletRequest request, HttpServletResponse response, InvalidOldPasswordException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_FORBIDDEN);
    }

    @ExceptionHandler({MailAuthenticationException.class})
    @ResponseBody
    public String mailAuth(HttpServletRequest request, HttpServletResponse response, MailAuthenticationException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_FORBIDDEN);
    }

    @ExceptionHandler(PagePersistenceException.class)
    @ResponseBody
    public String pagePersistence(final HttpServletRequest request, final HttpServletResponse response, final PagePersistenceException e) throws Exception {
        log.error(e.getMessage(), e);
        final HttpStatus status = PagePersistenceException.class.getAnnotation(ResponseStatus.class).code();
        return handleException(e, request, response, status.value());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public String accessDenied(final HttpServletRequest request, HttpServletResponse response, AccessDeniedException e){
        return handleException(e, request, response, HttpServletResponse.SC_FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String exception(HttpServletRequest request, HttpServletResponse response, Exception e) {

        return handleException(e, request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    }

}
