package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserRole;
import kpi.diploma.ovcharenko.repo.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Collection;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SignController {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void securityTest() throws Exception {
        this.mockMvc.perform(get("/add"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void successLoginTest() throws Exception {
        UserRole role = new UserRole();
        role.setName("ROLE_USER");
        AppUser user = new AppUser().toBuilder()
                .firstName("test")
                .lastName("test")
                .email("test@gmail.com")
                .password("test")
                .roles(Collections.singleton(role))
                .build();

        RequestBuilder request = post("/login.html")
                .param("firstname", user.getFirstName())
                .param("lastname", user.getLastName())
                .param("email", user.getEmail())
                .param("password", user.getPassword());


//        this.mockMvc.perform(post("/login")
//                .flashAttr("user", user)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(forwardedUrl("/"));

        this.mockMvc.perform(post("/login")
                .sessionAttr("user", user))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/"));
    }
}
