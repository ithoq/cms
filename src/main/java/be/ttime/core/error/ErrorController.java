package be.ttime.core.error;

import be.ttime.core.persistence.model.FileEntity;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

@RestController
public class ErrorController {

    @RequestMapping(value = "/errorController", method = RequestMethod.POST)
    public String handleError(ModelMap model, HttpServletRequest request) throws Exception {

        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        // String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        String exceptionMessage = getExceptionMessage(throwable, statusCode);

        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
        if (requestUri == null) {
            requestUri = "Unknown";
        }

        String message = MessageFormat.format("{0} returned for {1} with message {2}",
                statusCode, requestUri, exceptionMessage
        );

        throw new Exception(message);
    }

    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        if (throwable != null) {
            return throwable.toString();
        }
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.getReasonPhrase();
    }
}
