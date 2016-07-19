package be.ttime.core.config.security;

import be.ttime.core.error.IpLockedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("authenticationFailureHandler")
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private MessageSource messages;

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {

        String isAdmin = request.getParameter("isAdmin");
        String url = (isAdmin.equals("true") ? "/admin/login" : "/login");
        setDefaultFailureUrl( url +"?error=true");

        super.onAuthenticationFailure(request, response, exception);

        //final Locale locale = localeResolver.resolveLocale(request);

        String errorMessage = null;

        if (exception instanceof LockedException) {
            errorMessage = messages.getMessage("error.auth.disabled", null, LocaleContextHolder.getLocale());
        } else if (exception instanceof DisabledException){
            errorMessage = messages.getMessage("error.auth.disabled", null, LocaleContextHolder.getLocale());
        }
        else if (exception instanceof IpLockedException) {
            errorMessage = messages.getMessage("error.auth.ipBlocked", null, LocaleContextHolder.getLocale());
        } else {
            errorMessage = messages.getMessage("error.auth.badCredentials", null, LocaleContextHolder.getLocale());
        }

        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}