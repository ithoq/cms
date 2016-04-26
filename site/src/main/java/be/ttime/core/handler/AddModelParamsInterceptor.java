package be.ttime.core.handler;

import be.ttime.core.error.CmsNotInstalledException;
import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.util.CmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AddModelParamsInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private IApplicationService applicationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean installationURL = false;

        if(request.getMethod().equals("POST") && request.getRequestURI().equals("/admin/install")){
            installationURL = true;
        }
        if(!applicationService.getApplicationConfig().isAlreadyInstall() && !installationURL) {
            throw new CmsNotInstalledException();
        }

        return super.preHandle(request, response, handler);
    }

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
