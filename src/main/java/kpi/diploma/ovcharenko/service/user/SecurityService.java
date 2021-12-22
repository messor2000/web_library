package kpi.diploma.ovcharenko.service.user;

public interface SecurityService {
    String findLoggedInEmail();

    void autoLogin(String email, String password);
}
