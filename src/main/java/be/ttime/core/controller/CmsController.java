package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.field.PageData;
import be.ttime.core.persistence.dao.PageBlockEntity;
import be.ttime.core.persistence.dao.PageEntity;
import be.ttime.core.persistence.service.IPageBlockService;
import be.ttime.core.persistence.service.IPageService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Map;

@RestController
@Slf4j
public class CmsController {

    @Autowired
    private IPageService pageService;

    @Autowired
    private IPageBlockService pageBlockService;

    @Autowired
    private PebbleUtils pebbleUtils;

    public static String getCurrentUrl(HttpServletRequest request) {
        URI uri = null;
        try {
            URL url = new URL(request.getRequestURL().toString());
            String host = url.getHost();
            String userInfo = url.getUserInfo();
            String scheme = url.getProtocol();
            int port = url.getPort();
            String path = (String) request.getAttribute("javax.servlet.forward.request_uri");
            String query = (String) request.getAttribute("javax.servlet.forward.query_string");
            uri = new URI(scheme, userInfo, host, port, path, query, null);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return uri != null ? uri.toString() : "";
    }

    /**
     * Check if the request should be handle by the CMS controller or send to an error page
     *
     * @throws Exception
     */
    private void checkHttpStatus(HttpServletRequest request) throws Exception {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (statusCode != 404) {
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
    }

    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        if (throwable != null) {
            return throwable.toString();
        }
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.getReasonPhrase();
    }

    @RequestMapping(value = "cmsController", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String page(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        checkHttpStatus(request);

        String path = (String) request.getAttribute("javax.servlet.forward.request_uri");
        //String query = (String) request.getAttribute("javax.servlet.forward.query_string");
        PageEntity page = pageService.findBySlug(path);
        if (page == null) {
            throw new ResourceNotFoundException();
        }
        response.setStatus(200);
        if (!StringUtils.isEmpty(page.getData())) {
            Gson gson = new Gson();
            PageData pageData = gson.fromJson(page.getData(), PageData.class);
            model.put("data", pageData.getData());
            model.put("dataArray", pageData.getDataArray());
        }

        PageBlockEntity master = pageBlockService.findByNameAndBlockType("master", PageBlockEntity.BlockType.System);

        model.put("attr", CmsUtils.getAttributes(request));
        model.put("get", CmsUtils.getParameters(request));
        model.put("csrf", CmsUtils.getCsrfInput(request));
        model.put("title", page.getSeoTitle());
        model.put("main", pebbleUtils.parseBlock(page.getPageTemplate().getPageBlock(), model));
        return pebbleUtils.parseBlock(master, model);
    }

    @RequestMapping(value = "cmsController", method = RequestMethod.POST)
    public String pagePost(ModelMap model, HttpServletRequest request) throws Exception {

        checkHttpStatus(request);

        return "CMS controller POST";
    }
}
