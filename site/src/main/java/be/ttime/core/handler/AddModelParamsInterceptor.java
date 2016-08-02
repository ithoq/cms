package be.ttime.core.handler;

import be.ttime.core.error.CmsNotInstalledException;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.util.CmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AddModelParamsInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private IApplicationService applicationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean installationURL = false;

        if(request.getRequestURI().equals("/admin/install")){
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

        // When we use @RequestBody, modelAndView == null
        if(modelAndView != null) {
            boolean isRedirectView = modelAndView.getView() instanceof RedirectView;
            boolean viewNameStartsWithRedirect = (modelAndView.getViewName() == null ? false : modelAndView.getViewName().startsWith(UrlBasedViewResolver.REDIRECT_URL_PREFIX));

            if (modelAndView.hasView() && !isRedirectView && !viewNameStartsWithRedirect) {
                    CmsUtils.fillModelAndView(modelAndView, request, applicationService);
            }
        }
    }
}
