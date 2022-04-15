package kpi.diploma.ovcharenko.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController userController;
    @Mock
    private SecurityContext mockSecurityContext;

    @Test
    @DisplayName("Test without permision can't go to path")
    public void securityTest() throws Exception {
        this.mockMvc.perform(get("/add"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("Test redirection to main page if successful login")
    public void successLoginTest() throws Exception {
        RequestBuilder requestBuilder = formLogin().user("test@gmail.com").password("qwe");
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("Test throw an error because bad credentials")
    public void errorBecauseBadCredentialsLoginTest() throws Exception {
        RequestBuilder requestBuilder = formLogin().user("erbhjlgkher@gmail.com").password("qwe");
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", password = "qwe", roles = "USER")
    public void getCurrentUserTest() throws Exception {
        MvcResult res = mockMvc.perform(get("profile")).andExpect(status().isOk()).andReturn();
        assertEquals("test@gmail.com", res.getResponse().getContentAsString());
    }
}
