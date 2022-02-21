package kpi.diploma.ovcharenko.service.user;

import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    AppUser findByEmail(String email);

    AppUser save(UserModel registration);

    void takeBook(Long id, String userEmail);

    void returnBook(Long id, String userEmail);
}
