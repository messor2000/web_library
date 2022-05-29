package kpi.diploma.ovcharenko.service.activation;

import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.service.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final UserService userService;
    private final MessageSource messages;
    private final JavaMailSender mailSender;

    public RegistrationListener(UserService userService, MessageSource messages, JavaMailSender mailSender) {
        this.userService = userService;
        this.messages = messages;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        AppUser user = event.getUser();
//        String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, event.getToken());

        final SimpleMailMessage email = constructEmailMessage(event, user, event.getToken());
        mailSender.send(email);
    }

    private SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final AppUser user, final String token) {
        final String recipientAddress = user.getEmail();
        final String subject = "Registration Confirmation";
        final String confirmationUrl = event.getAppUrl() + "/registration/confirm?token=" + token;
        final String message = messages.getMessage("message.regSuccLink", null,
                "You registered successfully. To confirm your registration, please click on the below link.", event.getLocale());
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom("noreply@psonlibrary.com");
        return email;
    }
}
