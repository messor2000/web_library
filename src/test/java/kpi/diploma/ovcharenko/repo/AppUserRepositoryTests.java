package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.user.AppUser;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AppUserRepositoryTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Test find user by email")
    public void shouldReturnUserByEmail() {
        AppUser user = new AppUser().toBuilder()
                .firstName("test1")
                .lastName("test1")
                .email("test1@gmail.com")
                .password(passwordEncoder.encode("password"))
                .build();

        userRepository.save(user);

        AppUser foundUser = userRepository.findByEmail("test1@gmail.com");

        userRepository.delete(user);

        assertEquals(user.getEmail(), foundUser.getEmail());
    }
}
