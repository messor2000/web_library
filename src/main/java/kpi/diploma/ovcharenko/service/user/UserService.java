package kpi.diploma.ovcharenko.service.user;

import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    AppUser findByEmail(final String email);

    AppUser findById(final Long id);

    AppUser save(final UserModel registration);

    void deleteUser(Long id);

    void takeBook(final Long id, final String userEmail);

    void returnBook(final Long id, final String userEmail);

    void createPasswordResetTokenForUser(final AppUser user, final String token);

    Optional<AppUser> getUserByPasswordResetToken(final String token);

    void changeUserPassword(final AppUser user, final String password);

    boolean checkIfValidOldPassword(final AppUser user, final String oldPassword);

    List<AppUser> showAllUsers();

    void updateUser(Long userId, final UserModel userModel);
}
