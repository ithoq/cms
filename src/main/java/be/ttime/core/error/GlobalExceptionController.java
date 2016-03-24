package be.ttime.core.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * General error handler for the application.
 * see doc = https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
 */
@ControllerAdvice // => will make it a global exception handler (not individual)
@Slf4j
class GlobalExceptionController {
    /*
     * Handle exception NotFound
    */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView notFound(Exception exception, HttpServletResponse response) {
        System.out.println("Not found Exception");
        response.setStatus(404);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        log.debug(exception.toString());
        return new ModelAndView("error/notFound");
    }

    /**
     * Handle exceptions thrown by handlers.
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView exception(Exception exception, HttpServletResponse response) {
        log.error(exception.toString());

        response.setStatus(500);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        return new ModelAndView("error/general");
    }
}