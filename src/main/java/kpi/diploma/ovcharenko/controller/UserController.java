package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.service.user.LibrarySecurityService;
import kpi.diploma.ovcharenko.service.user.LibraryUserService;
import kpi.diploma.ovcharenko.service.user.SecurityService;
import kpi.diploma.ovcharenko.service.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Log4j2
@Controller
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;
    private final ModelMapper modelMapper;
    private final Environment env;
    private final MessageSource messages;
    private final JavaMailSender mailSender;

    public UserController(LibraryUserService userService, LibrarySecurityService securityService, ModelMapper modelMapper,
                          Environment env, MessageSource messages, JavaMailSender mailSender) {
        this.userService = userService;
        this.securityService = securityService;
        this.modelMapper = modelMapper;
        this.env = env;
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
    public String resetPassword(final HttpServletRequest request, final Model model, @RequestParam("email") final String userEmail,
                                RedirectAttributes redirectAttributes) {
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
        redirectAttributes.addAttribute("message", messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
        return "redirect:/login";
    }

    @GetMapping("/user/changePassword")
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

    @GetMapping("/emailError")
    public String redirectToEmailErrorPage() {
        return "emailError";
    }

    @GetMapping("/updatePassword")
    public String redirectToUpdatePasswordPage() {
        return "updatePassword";
    }

    private SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, AppUser user) {
        String url = contextPath + "/user/changePassword?token=" + token;
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
        email.setFrom(Objects.requireNonNull(env.getProperty("support.email")));
        return email;
    }
}


