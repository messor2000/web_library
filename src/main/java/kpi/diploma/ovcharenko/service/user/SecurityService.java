package kpi.diploma.ovcharenko.service.user;

public interface SecurityService {
    @SuppressWarnings("unused")
    String findLoggedInEmail();

    String validatePasswordResetToken(String token);

    @SuppressWarnings("unused")
    void autoLogin(String email, String password);
}
