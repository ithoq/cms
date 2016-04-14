package be.ttime.core.registration;

import be.ttime.core.persistence.model.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final UserEntity user;
    private final Locale locale;
    private final String appUrl;

    public OnRegistrationCompleteEvent(final UserEntity user, final Locale locale, final String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}

