package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.config.PasswordEncoder;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.PasswordResetToken;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureMockMvc
public class PasswordResetTokenTests {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private UserService userService;

    private final String token = UUID.randomUUID().toString();

    UserModel user = new UserModel().toBuilder()
            .firstName("test1")
            .lastName("test1")
            .email("test1@gmail.com")
            .password(PasswordEncoder.passwordEncoder().encode("password"))
            .build();

    @BeforeEach
    public void initEach() {
        userService.save(user);
    }

    @AfterEach
    public void deleteEach() {
        userService.deleteUser(userService.findByEmail(user.getEmail()).getId());
    }

    @Test
    @DisplayName("should return correct token by user id")
    void findTokenByUserTest() {
        AppUser foundUser = userService.findByEmail(user.getEmail());
        userService.createPasswordResetTokenForUser(foundUser, token);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUserId(foundUser.getId());
        passwordResetTokenRepository.delete(passwordResetToken);

        assertEquals(token, passwordResetToken.getToken());
    }

    @Test
    @DisplayName("should return correct token by token for rewriting it")
    void findTokenByTokenTest() {
        AppUser foundUser = userService.findByEmail(user.getEmail());
        userService.createPasswordResetTokenForUser(foundUser, token);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        passwordResetTokenRepository.delete(passwordResetToken);

        assertEquals(token, passwordResetToken.getToken());
    }

    @Test
    @Transactional
    @DisplayName("should successfully delete token by token id")
    void deleteTokenTest() {
        AppUser foundUser = userService.findByEmail(user.getEmail());
        userService.createPasswordResetTokenForUser(foundUser, token);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        passwordResetTokenRepository.deletePasswordResetTokenById(passwordResetToken.getId());

        assertNull(passwordResetTokenRepository.findByToken(token));
    }
}
