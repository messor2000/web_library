package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.config.PasswordEncoder;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class AppUserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("should return user after creating him")
    void shouldReturnUserByEmail() {
        AppUser user = new AppUser().toBuilder()
                .firstName("test1")
                .lastName("test1")
                .email("test1@gmail.com")
                .password(PasswordEncoder.passwordEncoder().encode("password"))
                .build();

        userRepository.save(user);

        AppUser foundUser = userRepository.findByEmail("test1@gmail.com");

        userRepository.delete(user);

        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    @Transactional
    @DisplayName("should return nothing after deleting user by id")
    void deleteUserByIdTest() {
        String email = "test1@gmail.com";
        AppUser user = new AppUser().toBuilder()
                .firstName("test1")
                .lastName("test1")
                .email(email)
                .password(PasswordEncoder.passwordEncoder().encode("password"))
                .build();

        userRepository.save(user);

        userRepository.deleteUserById(user.getId());

        assertNull(userRepository.findByEmail(email));
    }
}
