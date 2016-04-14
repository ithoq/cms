package be.ttime.core.handler;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlLocaleChangeInterceptor extends HandlerInterceptorAdapter {

    /**
     * Default name of the locale specification parameter: "locale".
     */
    public static final String DEFAULT_COOKIE_NAME = "lang";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        if (modelAndView != null) {
            if (modelAndView.getModel() != null) {
                if (StringUtils.hasText(DEFAULT_COOKIE_NAME)) {
                    modelAndView.getModelMap().addAttribute(DEFAULT_COOKIE_NAME, RequestContextUtils.getLocaleResolver(request).resolveLocale(request));
                }
            }
        }
    }
}