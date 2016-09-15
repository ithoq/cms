package be.ttime.core.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {


    @RequestMapping(value = "/error")
    @ResponseBody
    public String handleError(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.error("GRAVE - CustomErrorController", getThrowable(request));
        int code = getExceptionCode(request);
        if( code == 404){
            throw new ResourceNotFoundException("CustomErrorController", getThrowable(request));
        } else {
            throw new Exception("CustomErrorController", getThrowable(request));
        }
    }
    private Integer getExceptionCode(HttpServletRequest request) {
        return (Integer) request.getAttribute("javax.servlet.error.status_code");
    }

    private Throwable getThrowable(HttpServletRequest request){
        return (Throwable) request.getAttribute("javax.servlet.error.exception");
    }

    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        if (throwable != null) {
            return throwable.toString();
        }
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.getReasonPhrase();
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
