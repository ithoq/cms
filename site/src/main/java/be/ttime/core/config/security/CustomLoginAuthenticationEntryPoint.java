package be.ttime.core.config.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CustomLoginAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

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
        String test = "testest";
        String test2 = request.getRequestURI();
                     return "/login";
    }
}
