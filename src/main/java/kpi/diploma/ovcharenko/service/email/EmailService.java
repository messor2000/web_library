package kpi.diploma.ovcharenko.service.email;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    void sendEmail(SimpleMailMessage email);
}
