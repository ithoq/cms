package be.ttime.core.error;

import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    private  String handleException(final Exception e, HttpServletRequest request, final HttpServletResponse response, int sc, final String view, final String messageCode) {
        log.error(request.getRequestURI() + " - " + e.getMessage(), e);

        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setStatus(sc);
        ModelMap model = new ModelMap();
        Locale locale = LocaleContextHolder.getLocale();
        String errorBlock = sc == 404 ? "error.404" : "error";
        if(CmsUtils.uriIsAdmin(request)){
            String redirection = sc == 404 ? "/admin/error404" : "/admin/error";
            String rep = null;
            try {
                response.sendRedirect(redirection);
            } catch(Exception e2){
                rep= "Error " + sc;
            }
            return rep;
        } else {

            BlockEntity master = blockService.find(CmsUtils.BLOCK_PAGE_MASTER);
            BlockEntity login = blockService.find(errorBlock);

            CmsUtils.fillModelMap(model,request, applicationService);

            model.put("title", messageSource.getMessage(errorBlock, null, locale));
            String result = null;

            try {
                model.put("main", pebbleUtils.parseBlock(login, model));
                result = pebbleUtils.parseBlock(master, model);
            } catch(Exception e2){
                log.error("Exception in the error controller", e2);
            }

            return result;
        }
    }

    @ExceptionHandler({CmsNotInstalledException.class})
    public String notInstalled() {
        return "redirect:/admin/install";
    }

    @ExceptionHandler({CmsInMaintenanceException.class})
    public String maintenance() {
        return "redirect:/r/maintenance";
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    @ResponseBody
    public String notFound(HttpServletRequest request, HttpServletResponse response, ResourceNotFoundException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_NOT_FOUND, VIEW_404, "error.notFound");
    }

    @ExceptionHandler({UserNotFoundException.class})
    @ResponseBody
    public String notFound(HttpServletRequest request, HttpServletResponse response, UserNotFoundException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_NOT_FOUND, VIEW_GENERAL, "error.auth.userNotFound");
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    @ResponseBody
    public String userExist(HttpServletRequest request, HttpServletResponse response, UserAlreadyExistException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_CONFLICT, VIEW_GENERAL, "error.auth.userExist");
    }

    @ExceptionHandler({InvalidOldPasswordException.class})
    @ResponseBody
    public String invalidPassword(HttpServletRequest request, HttpServletResponse response, InvalidOldPasswordException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_FORBIDDEN, VIEW_GENERAL, "error.auth.invalidOldPassword");
    }

    @ExceptionHandler({MailAuthenticationException.class})
    @ResponseBody
    public String mailAuth(HttpServletRequest request, HttpServletResponse response, MailAuthenticationException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_FORBIDDEN, VIEW_GENERAL, "error.mail.config");
    }

    @ExceptionHandler(PagePersistenceException.class)
    @ResponseBody
    public String pagePersistence(final HttpServletRequest request, final HttpServletResponse response, final PagePersistenceException e) throws Exception {
        log.error(e.getMessage(), e);
        final HttpStatus status = PagePersistenceException.class.getAnnotation(ResponseStatus.class).code();
        return handleException(e, request, response, status.value(), e.viewName, e.errorKey);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String exception(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
            return handleException(e, request, response, HttpServletResponse.SC_FORBIDDEN, VIEW_GENERAL, "error.forbidden");
        } else {
            return handleException(e, request, response, response.getStatus(), VIEW_GENERAL, "error.general");
        }
    }

}
