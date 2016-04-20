package be.ttime.core.config.security;

import be.ttime.core.persistence.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private IUserService userService;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {
        final WebAuthenticationDetails auth = (WebAuthenticationDetails) e.getAuthentication().getDetails();
        this.userService.resetFailAttempts(e.getAuthentication().getName());
    }

}
