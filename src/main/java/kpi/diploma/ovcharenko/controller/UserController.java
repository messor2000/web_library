package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.book.LibraryBookService;
import kpi.diploma.ovcharenko.service.user.LibrarySecurityService;
import kpi.diploma.ovcharenko.service.user.LibraryUserService;
import kpi.diploma.ovcharenko.service.user.SecurityService;
import kpi.diploma.ovcharenko.service.user.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Set;

@Controller
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;

    public UserController(LibraryUserService userService, LibrarySecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new AppUser());

        return "registration";
    }

    @ModelAttribute("user")
    public UserModel userRegistrationDto() {
        return new UserModel();
    }

    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("user") @Valid UserModel userModel, BindingResult result) {
        AppUser existing = userService.findByEmail(userModel.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            return "registration";
        }

        userService.save(userModel);

        securityService.autoLogin(userModel.getEmail(), userModel.getConfirmPassword());

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String viewUserProfile(Model model) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);
        Set<Book> books = user.getBooks();

        model.addAttribute("appUser", user);
        model.addAttribute("userBooks", books);

        return "userProfile";
    }

    @PostMapping("/takeBook/{id}")
    public String takeBook(@PathVariable(name = "id") Long id) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);

        userService.takeBook(id, user.getEmail());

        return "redirect:/";
    }

    @PostMapping("/returnBook/{id}")
    public String returnBook(@PathVariable(name = "id") Long id) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);

        userService.returnBook(id, user.getEmail());

        return "redirect:/";
    }
}


// test1