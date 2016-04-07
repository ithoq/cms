package be.ttime.core.config.security;


import be.ttime.core.persistence.model.UserAttemptsEntity;
import be.ttime.core.persistence.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("authenticationProvider")
public class LimitLoginAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private IUserService userService;

    @Autowired
    @Qualifier("userService")
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        try {
            WebAuthenticationDetails details = (WebAuthenticationDetails)authentication.getDetails();

            //this.userService.checkIpAttempts(details.getRemoteAddress());

            Authentication auth = super.authenticate(authentication);

            //if reach here, means login success, else an exception will be thrown
            //reset the user_attempts
            this.userService.resetFailAttempts(authentication.getName());
            return auth;

        } catch (BadCredentialsException e) {

            //invalid login, update to user_attempts
            this.userService.updateFailAttempts(authentication.getName());
            throw e;

        } catch (LockedException e){

            //this user is locked!
            String error;
            UserAttemptsEntity userAttemptsEntity = userService.getUserAttempts(authentication.getName());

            if(userAttemptsEntity !=null){
                Date lastAttempts = userAttemptsEntity.getLastModified();
                error = "User account is locked! <br><br>Username : "
                        + authentication.getName() + "<br>Last Attempts : " + lastAttempts;
            }else{
                error = e.getMessage();
            }

            throw new LockedException(error);
        }

    }

}