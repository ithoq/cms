package be.ttime.core.error;

import be.ttime.core.persistence.service.IApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.ui.Model;
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
    @Autowired
    IApplicationService applicationService;

    public static ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        log.error(request.getRequestURI() + " - " + ex.getMessage(), ex);

        if (ex instanceof ResourceNotFoundException) {
            return handleException(HttpServletResponse.SC_NOT_FOUND, request, response, VIEW_404, "error.notFound");
        } else if (ex instanceof UserNotFoundException) {
            return handleException(HttpServletResponse.SC_NOT_FOUND, request, response, VIEW_GENERAL, "error.auth.userNotFound");
        } else if (ex instanceof UserAlreadyExistException) {
            return handleException(HttpServletResponse.SC_NOT_FOUND, request, response, VIEW_GENERAL, "error.auth.userExist");
        } else if (ex instanceof InvalidOldPasswordException) {
            return handleException(HttpServletResponse.SC_NOT_FOUND, request, response, VIEW_GENERAL, "error.auth.invalidOldPassword");
        } else if (ex instanceof MailAuthenticationException) {
            return handleException(HttpServletResponse.SC_NOT_FOUND, request, response, VIEW_GENERAL, "error.mail.config");
        } else if (response.getStatus() == 403) {
            return handleException(HttpServletResponse.SC_FORBIDDEN, request, response, VIEW_GENERAL, "error.forbidden");
        } else {
            return handleException(response.getStatus(), request, response, VIEW_GENERAL, "error.general");
        }
    }

    private static ModelAndView handleException(int sc, HttpServletRequest request, HttpServletResponse response, String view, String messageCode) {
        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setStatus(sc);
        response.setStatus(response.getStatus());
        ModelAndView mv = new ModelAndView(view);
        mv.addObject("msg", messageCode);
        return mv;
    }

    @ExceptionHandler({CmsNotInstalledException.class})
    public String notInstalled(HttpServletResponse response, HttpServletRequest request, Model model) {

      /*  try {
            Locale[] locales = Arrays.copyOfRange(Locale.getAvailableLocales(), 1, Locale.getAvailableLocales().length);
            Arrays.sort(locales, (l1, l2) -> l1.getDisplayName().compareTo(l2.getDisplayName()));

            List<Locale> result = new ArrayList<>();

            // remove special locale
            for (Locale locale : locales) {
                if(locale.toString().length() <= 5){
                    result.add(locale);
                }
            }

            model.addAttribute("locales", result);
            model.addAttribute("csrf", CmsUtils.getCsrfInput(request));
            model.addAttribute("adminLocales", applicationService.getAdminlanguages());
        }
        catch(Exception e){
            log.error(e.toString());
        }*/
        return "redirect:/admin/install";
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ModelAndView notFound(HttpServletRequest request, HttpServletResponse response, ResourceNotFoundException ex) throws IOException {
        return doResolveException(request, response, ex);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ModelAndView notFound(HttpServletRequest request, HttpServletResponse response, UserNotFoundException ex) throws IOException {
        return doResolveException(request, response, ex);
    }

    @ExceptionHandler({InvalidOldPasswordException.class})
    public ModelAndView invalidPassword(HttpServletRequest request, HttpServletResponse response, InvalidOldPasswordException ex) throws IOException {
        return doResolveException(request, response, ex);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public ModelAndView userExist(HttpServletRequest request, HttpServletResponse response, UserAlreadyExistException ex) throws IOException {
        return doResolveException(request, response, ex);
    }

    @ExceptionHandler({MailAuthenticationException.class})
    public ModelAndView mailAuth(HttpServletRequest request, HttpServletResponse response, MailAuthenticationException ex) throws IOException {
        return doResolveException(request, response, ex);
    }

    @ExceptionHandler(PagePersistenceException.class)
    public ModelAndView pagePersistence(final HttpServletRequest request, final HttpServletResponse response, final PagePersistenceException e) throws Exception {
        log.error(e.getMessage(), e);
        final HttpStatus status = PagePersistenceException.class.getAnnotation(ResponseStatus.class).code();
        return handleException(status.value(), request, response, e.viewName(), e.errorKey());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView exception(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        return doResolveException(request, response, ex);
    }
}
