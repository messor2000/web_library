package kpi.diploma.ovcharenko.service.user;

import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.entity.user.VerificationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    AppUser findByEmail(final String email);

    AppUser findById(final Long id);

    AppUser save(final UserModel registration);

    void createNewUserByAdmin(UserModel userModel);

    void saveRegisteredUser(final AppUser user);

    void deleteUser(Long id);

    void bookedBook(final Long id, final String userEmail);

    void approveBookForUser(final Long bookCardId);

    void rejectTheBook(Long bookCardId);

    void returnTheBook(final Long bookCardId);

    void deleteBookCard(final Long bookCardId);

    void createVerificationTokenForUser(final AppUser user, final String token);

    VerificationToken getVerificationToken(String token);

    void createPasswordResetTokenForUser(final AppUser user, final String token);

    Optional<AppUser> getUserByPasswordResetToken(final String token);

    void changeUserPassword(final AppUser user, final String password);

    boolean checkIfValidOldPassword(final AppUser user, final String oldPassword);

    List<AppUser> showAllUsers();

    void updateUser(Long userId, final UserModel userModel);

    void addPhotoImage(MultipartFile file, String email);

    void changePhotoImage(MultipartFile file, String email);
}
