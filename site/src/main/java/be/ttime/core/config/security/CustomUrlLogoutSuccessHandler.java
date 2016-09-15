package be.ttime.core.config.security;

import be.ttime.core.util.CmsUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomUrlLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    public CustomUrlLogoutSuccessHandler(){
        super();
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        boolean isAdmin = CmsUtils.refererIsAdmin(request);
        boolean sessionExpiration = request.getParameterMap().containsKey("sessionExpiration");

        String path = isAdmin ? "/admin/login" : "/login";
        path += sessionExpiration ? "?sessionExpiration" : "?logout";
        return path;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onLogoutSuccess(request, response, authentication);
    }
}
