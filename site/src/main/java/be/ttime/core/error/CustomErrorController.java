package be.ttime.core.error;

import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.util.PebbleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

    @Autowired
    private IBlockService blockService;
    @Autowired
    private IApplicationService applicationService;
    @Autowired
    private PebbleUtils pebbleUtils;
    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "admin/error404")
    public String admin404(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setStatus(404);
        model.put("code", 404);
        return "admin/error/general";
    }

    @RequestMapping(value = "admin/error")
    public String adminError(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(500);
        model.put("code", getExceptionCode(request));
        model.put("msg", getExceptionMessage(request));
        return "admin/error/general";
    }
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

    private String getExceptionMessage(HttpServletRequest request){
        Integer statusCode = getExceptionCode(request);
        Throwable throwable = getThrowable(request);
        if(statusCode == null || throwable == null) return null;
        // String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        String exceptionMessage = getExceptionMessage(throwable, statusCode);

        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
        if (requestUri == null) {
            requestUri = "Unknown";
        }

        return MessageFormat.format("{0} returned for {1} with message {2}",
                statusCode, requestUri, exceptionMessage
        );
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
