package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.exception.ValidPassportException;
import kpi.diploma.ovcharenko.service.user.LibrarySecurityService;
import kpi.diploma.ovcharenko.service.user.SecurityService;
import kpi.diploma.ovcharenko.service.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Controller
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;
    private final MessageSource messages;
    private final JavaMailSender mailSender;

    public UserController(UserService userService, LibrarySecurityService securityService,
                          MessageSource messages, JavaMailSender mailSender) {
        this.userService = userService;
        this.securityService = securityService;
        this.messages = messages;
        this.mailSender = mailSender;
    }

    @GetMapping("/login")
    public ModelAndView login(@RequestParam("error") final Optional<String> error, ModelMap model) {
        error.ifPresent(e -> model.addAttribute("error", e));

        return new ModelAndView("login", model);
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

    @GetMapping("/forgetPassword")
    public String showForgetPasswordPage() {
        return "forgetPassword";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(final HttpServletRequest request, final Model model, @RequestParam("email") final String userEmail) {
        final AppUser user = userService.findByEmail(userEmail);
        if (user == null) {
            model.addAttribute("message", messages.getMessage("message.userNotFound", null, request.getLocale()));
            return "redirect:/login";
        }

        final String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        try {
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            final SimpleMailMessage email = constructResetTokenEmail(appUrl, request.getLocale(), token, user);
            mailSender.send(email);
        } catch (final MailAuthenticationException e) {
            log.debug("MailAuthenticationException", e);
            return "redirect:/emailError";
        } catch (final Exception e) {
            log.debug(e.getLocalizedMessage(), e);
            model.addAttribute("message", e.getLocalizedMessage());
            return "redirect:/login";
        }
        return "infoAboutEmail";
    }

    @GetMapping("/user/resetOldPassword")
    public String showChangePasswordPage(Locale locale, @RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        String result = securityService.validatePasswordResetToken(token);
        if (result != null) {
            String message = messages.getMessage("auth.message." + result, null, locale);
            return "redirect:/login" + "&message=" + message;
        } else {
            redirectAttributes.addAttribute("token", token);
            return "redirect:/updatePassword";
        }
    }

    @PostMapping("/user/savePassword")
    public String savePassword(@RequestParam("password") final String password, @RequestParam("token") String token) {
        Optional<AppUser> user = userService.getUserByPasswordResetToken(token);
        user.ifPresent(appUser -> userService.changeUserPassword(appUser, password));
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String viewUserProfile(Model model) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);
        List<Book> books = user.getBooks();
        List<AppUser> appUsers = userService.showAllUsers();

        model.addAttribute("appUser", user);
        model.addAttribute("userBooks", books);
        model.addAttribute("appUsers", appUsers);

        return "userProfile";
    }

    @GetMapping("/user/update/profile")
    public String showUpdateProfileForm(Model model) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);

        model.addAttribute("user", user);
        return "updateUserProfile";
    }

    @PostMapping("/user/update/profile")
    public String updateUserProfile(Model model, @ModelAttribute("user") @Valid UserModel userModel) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);
        userService.updateUser(user.getId(), userModel);
        List<Book> books = user.getBooks();
        List<AppUser> appUsers = userService.showAllUsers();

        model.addAttribute("appUser", user);
        model.addAttribute("userBooks", books);
        model.addAttribute("appUsers", appUsers);
        return "redirect:/profile";
    }

    @GetMapping("/user/changePassword")
    public String showChangePasswordPage() {
        return "changePassword";
    }

    @PostMapping("/user/updatePassword")
    public String changeUserPassword(@RequestParam("password") String password,
                                     @RequestParam("oldpassword") String oldPassword) throws ValidPassportException {
        AppUser user = userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!userService.checkIfValidOldPassword(user, oldPassword)) {
            throw new ValidPassportException("Old password is invalid");
        }
        userService.changeUserPassword(user, password);
        return "redirect:/login";
    }

    @PostMapping("/takeBook/{id}")
    public String takeBook(@PathVariable(name = "id") Long id) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);

        userService.takeBook(id, currentUser);

        return "redirect:/";
    }

    @PostMapping("/returnBook/{id}")
    public String returnBook(@PathVariable(name = "id") Long id) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);

        userService.returnBook(id, user.getEmail());

        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/allUsers")
    public String showAllUsers(Model model) {
        List<AppUser> appUsers = userService.showAllUsers();

        model.addAttribute("appUsers", appUsers);
        return "tableUsers";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/updateUser/{id}")
    public String showUserUpdatePage(@PathVariable("id") Long id, Model model) {
        AppUser user = userService.findById(id);

        model.addAttribute("user", user);
        return "updateUserByAdmin";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/updateUser/{id}")
    public String updateUser(Model model, @PathVariable("id") Long id, @ModelAttribute("user") @Valid UserModel userModel) {
        userService.updateUser(id, userModel);
        List<AppUser> appUsers = userService.showAllUsers();

        model.addAttribute("appUsers", appUsers);
        return "redirect:/admin/allUsers";
    }


    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);
        List<AppUser> appUsers = userService.showAllUsers();

        userService.deleteUser(id);

        model.addAttribute("appUser", user);
        model.addAttribute("appUsers", appUsers);
        return "redirect:/profile";
    }

    @GetMapping("/emailError")
    public String redirectToEmailErrorPage() {
        return "emailError";
    }

    @GetMapping("/updatePassword")
    public String redirectToUpdatePasswordPage() {
        return "updatePassword";
    }

    private SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, AppUser user) {
        String url = contextPath + "/user/resetOldPassword?token=" + token;
        String message = messages.getMessage("message.resetPassword",
                null, locale);
        return constructEmail(message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String body, AppUser user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("noreply@psonlibrary.com");
        email.setSubject("Reset Password");
        email.setText(body);
        email.setTo(user.getEmail());
        return email;
    }
}


