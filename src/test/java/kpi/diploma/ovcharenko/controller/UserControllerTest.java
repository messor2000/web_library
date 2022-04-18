package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.repo.UserRepository;
import kpi.diploma.ovcharenko.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
//    @Autowired
//    private UserController userController;
//    @Mock
//    private SecurityContext mockSecurityContext;

    @Autowired
    private UserService userService;


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

//    @Test
//    public void findById_TodoEntryNotFound_ShouldRender404View() throws Exception {
//        when(todoServiceMock.findById(1L)).thenThrow(new TodoNotFoundException(""));
//
//        mockMvc.perform(get("/todo/{id}", 1L))
//                .andExpect(status().isNotFound())
//                .andExpect(view().name("error/404"))
//                .andExpect(forwardedUrl("/WEB-INF/jsp/error/404.jsp"));
//
//        verify(todoServiceMock, times(1)).findById(1L);
//        verifyZeroInteractions(todoServiceMock);
//    }
}
