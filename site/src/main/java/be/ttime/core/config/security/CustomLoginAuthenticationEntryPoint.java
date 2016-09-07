package be.ttime.core.config.security;

import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.util.CmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Component
public class CustomLoginAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    @Autowired
    private IApplicationService applicationService;

    public CustomLoginAuthenticationEntryPoint() {
        //  LoginUrlAuthenticationEntryPoint requires a default
        super("/");
    }

    /**
     * @param request   the request
     * @param response  the response
     * @param exception the exception
     * @return the URL (cannot be null or empty; defaults to {@link #getLoginFormUrl()})
     */
    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
                                                     AuthenticationException exception) {

        String loginUrl = null;
        if(CmsUtils.uriIsAdmin(request)){
            loginUrl = "/admin/login";
        } else {
            Locale locale = LocaleContextHolder.getLocale();
            String localeString = null;
            localeString = (locale != null) ? locale.toString() : applicationService.getDefaultSiteLang();
            loginUrl = "/" + localeString + "/login";
        }

        return loginUrl;
    }
}
