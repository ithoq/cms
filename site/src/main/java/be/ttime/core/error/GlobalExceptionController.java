package be.ttime.core.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * General error handler for the application.
 * see doc = https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
 */
@ControllerAdvice // => will make it a global exception handler (not individual)
@Slf4j
class GlobalExceptionController {

    @Autowired
    private MessageSource messages;

    private final static String VIEW_GENERAL = "error/general";
    private final static String VIEW_404 = "error/notFound";
    private final static String ROUTE_GENERAL_ERROR = "/error";

    @ExceptionHandler({ResourceNotFoundException.class})
    public ModelAndView notFound(HttpServletRequest request, HttpServletResponse response, ResourceNotFoundException ex) throws IOException {
        return doResolveException(request,response,ex);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ModelAndView notFound(HttpServletRequest request, HttpServletResponse response, UserNotFoundException ex) throws IOException {
        return doResolveException(request,response,ex);
    }

    @ExceptionHandler({InvalidOldPasswordException.class})
    public ModelAndView invalidPassword(HttpServletRequest request, HttpServletResponse response, InvalidOldPasswordException ex) throws IOException {
        return doResolveException(request,response,ex);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public ModelAndView userExist(HttpServletRequest request, HttpServletResponse response, UserAlreadyExistException ex) throws IOException {
        return doResolveException(request,response,ex);
    }

    @ExceptionHandler({MailAuthenticationException.class})
    public ModelAndView mailAuth(HttpServletRequest request, HttpServletResponse response, MailAuthenticationException ex) throws IOException {
        return doResolveException(request,response,ex);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView exception(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        return doResolveException(request,response,ex);
    }

    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Exception ex) {

        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            if (ex instanceof ResourceNotFoundException) {
                log.debug(request.getRequestURI() + " - " + ex.getMessage()+ " - " + ex.getCause());
                return handleException(HttpServletResponse.SC_NOT_FOUND, request, response, VIEW_404, "error.notFound");
            }
            else if(ex instanceof UserNotFoundException) {
                log.debug(request.getRequestURI() + " - " + ex.getMessage()+ " - " + ex.getCause());
                return handleException(HttpServletResponse.SC_NOT_FOUND, request, response, VIEW_GENERAL, "error.auth.userNotFound");
            }
            else if(ex instanceof UserAlreadyExistException) {
                log.debug(request.getRequestURI() + " - " + ex.getMessage()+ " - " + ex.getCause());
                return handleException(HttpServletResponse.SC_NOT_FOUND, request, response, VIEW_GENERAL, "error.auth.userExist");
            }
            else if(ex instanceof InvalidOldPasswordException) {
                log.debug(request.getRequestURI() + " - " + ex.getMessage() + " - " + ex.getCause());
                return handleException(HttpServletResponse.SC_NOT_FOUND, request, response, VIEW_GENERAL, "error.auth.invalidOldPassword");
            }
            else if(ex instanceof MailAuthenticationException) {
                log.debug(request.getRequestURI() + " - " + ex.getMessage()+ " - " + ex.getCause());
                return handleException(HttpServletResponse.SC_NOT_FOUND, request, response, VIEW_GENERAL, "error.mail.config");
            }
            else{
                log.debug(request.getRequestURI() + " - " + ex.getMessage()+ " - " + ex.getCause());
                return handleException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, request, response, VIEW_GENERAL, "error.general");
            }
        }
        catch (Exception handlerException) {
            log.error("Handling of [" + ex.getClass().getName() + "] resulted in Exception", handlerException);
        }
        return null;
    }

    private ModelAndView handleException(int sc, HttpServletRequest request, HttpServletResponse response, String view, String messageCode) throws Exception  {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setStatus(sc);
        ModelAndView mv = new ModelAndView(view);
        mv.addObject("msg", messages.getMessage(messageCode, null, request.getLocale()));
        return mv;
    }
}
