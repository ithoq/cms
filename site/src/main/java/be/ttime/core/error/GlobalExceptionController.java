package be.ttime.core.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    public final static String VIEW_GENERAL = "error/general";
    private final static String VIEW_404 = "error/notFound";

    private static ModelAndView handleException(final Exception e, final HttpServletRequest request, final HttpServletResponse response, int sc, final String view, final String messageCode) {
        log.error(request.getRequestURI() + " - " + e.getMessage(), e);

        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setStatus(sc);

        final ModelAndView mv = new ModelAndView(view);
        mv.addObject("msg", messageCode);
        return mv;
    }

    @ExceptionHandler({CmsNotInstalledException.class})
    public String notInstalled() {
        return "redirect:/admin/install";
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ModelAndView notFound(HttpServletRequest request, HttpServletResponse response, ResourceNotFoundException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_NOT_FOUND, VIEW_404, "error.notFound");
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ModelAndView notFound(HttpServletRequest request, HttpServletResponse response, UserNotFoundException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_NOT_FOUND, VIEW_GENERAL, "error.auth.userNotFound");
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public ModelAndView userExist(HttpServletRequest request, HttpServletResponse response, UserAlreadyExistException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_CONFLICT, VIEW_GENERAL, "error.auth.userExist");
    }

    @ExceptionHandler({InvalidOldPasswordException.class})
    public ModelAndView invalidPassword(HttpServletRequest request, HttpServletResponse response, InvalidOldPasswordException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_FORBIDDEN, VIEW_GENERAL, "error.auth.invalidOldPassword");
    }

    @ExceptionHandler({MailAuthenticationException.class})
    public ModelAndView mailAuth(HttpServletRequest request, HttpServletResponse response, MailAuthenticationException e) throws IOException {
        return handleException(e, request, response, HttpServletResponse.SC_FORBIDDEN, VIEW_GENERAL, "error.mail.config");
    }

    @ExceptionHandler(PagePersistenceException.class)
    public ModelAndView pagePersistence(final HttpServletRequest request, final HttpServletResponse response, final PagePersistenceException e) throws Exception {
        log.error(e.getMessage(), e);
        final HttpStatus status = PagePersistenceException.class.getAnnotation(ResponseStatus.class).code();
        return handleException(e, request, response, status.value(), e.viewName, e.errorKey);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView exception(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
            return handleException(e, request, response, HttpServletResponse.SC_FORBIDDEN, VIEW_GENERAL, "error.forbidden");
        } else {
            return handleException(e, request, response, response.getStatus(), VIEW_GENERAL, "error.general");
        }
    }

}
