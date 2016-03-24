package be.ttime.core.handler;

import be.ttime.core.util.CmsUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddModelParamsInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);

        if (modelAndView != null) { // When we use @RequestBody, modelAndView == null
            modelAndView.addObject("user", CmsUtils.getCurrentUser());
            modelAndView.addObject("attr", CmsUtils.getAttributes(request));
            modelAndView.addObject("get", CmsUtils.getParameters(request));
            modelAndView.addObject("csrf", CmsUtils.getCsrfInput(request));
        }
    }
}
