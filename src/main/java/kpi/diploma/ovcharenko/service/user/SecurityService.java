package kpi.diploma.ovcharenko.service.user;

public interface SecurityService {
    String findLoggedInEmail();

    String validatePasswordResetToken(String token);

    void autoLogin(String email, String password);
}
