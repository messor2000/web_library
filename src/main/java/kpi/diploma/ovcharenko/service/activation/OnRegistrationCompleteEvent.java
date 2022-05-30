package kpi.diploma.ovcharenko.service.activation;

import kpi.diploma.ovcharenko.entity.user.AppUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private AppUser user;
    private String token;

    public OnRegistrationCompleteEvent(AppUser user, Locale locale, String appUrl, String token) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
        this.token = token;
    }
}
