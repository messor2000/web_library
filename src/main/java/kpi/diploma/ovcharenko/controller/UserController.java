package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.card.CardStatus;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.exception.ValidPassportException;
import kpi.diploma.ovcharenko.service.book.cards.BookCardService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Log4j2
@Controller
public class UserController {

    private final UserService userService;
    private final BookCardService bookCardService;
    private final SecurityService securityService;
    private final MessageSource messages;
    private final JavaMailSender mailSender;

    public UserController(UserService userService, LibrarySecurityService securityService,
                          MessageSource messages, JavaMailSender mailSender, BookCardService bookCardService) {
        this.userService = userService;
        this.securityService = securityService;
        this.messages = messages;
        this.mailSender = mailSender;
        this.bookCardService = bookCardService;
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
            return "redirect:/emailError";
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
        List<BookCard> bookCards = bookCardService.findAllUserBookCards(user.getId());

        Set<CardStatus> cardStatuses = new HashSet<>();

        for (BookCard bookCard : bookCards) {
            cardStatuses.add(bookCard.getCardStatus());
        }

        log.info(cardStatuses);

        model.addAttribute("appUser", user);
        model.addAttribute("bookCards", bookCards);
        model.addAttribute("bookCardsWithStatus", bookCards);

        return "userProfile";
    }

    @GetMapping("/profile/{bookStatus}")
    public String viewUserProfileWithBooks(Model model, @PathVariable("bookStatus") CardStatus status) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);
        List<BookCard> bookCardsWithStatus = bookCardService.findAllUserBookCardsAndStatus(user.getId(), status);
        Set<CardStatus> cardStatuses = new HashSet<>();

        for (BookCard bookCard : bookCardsWithStatus) {
            cardStatuses.add(bookCard.getCardStatus());
        }

        log.info(cardStatuses);
        model.addAttribute("appUser", user);
        model.addAttribute("cardStatuses", cardStatuses);
        model.addAttribute("bookCardsWithStatus", bookCardsWithStatus);

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
        List<BookCard> bookCards = bookCardService.findAllUserBookCards(user.getId());
        List<AppUser> appUsers = userService.showAllUsers();

        model.addAttribute("appUser", user);
        model.addAttribute("bookCards", bookCards);
        model.addAttribute("appUsers", appUsers);
        return "redirect:/profile";
    }

    @GetMapping("/user/change/password")
    public String showChangePasswordPage() {
        return "changePassword";
    }

    @PostMapping("/user/update/password")
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
    public String takeBook(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        userService.bookedBook(id, currentUser);

        return getPreviousPageByRequest(request).orElse("/");
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/allUsers")
    public String showAllUsers(Model model) {
        List<AppUser> appUsers = userService.showAllUsers();

        model.addAttribute("appUsers", appUsers);
        return "tableUsers";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/checkUserBookCards/{id}")
    public String showAllTakenBooks(Model model, @PathVariable("id") Long id) {
        List<BookCard> bookCards = bookCardService.findAllUserBookCards(id);

        model.addAttribute("bookCards", bookCards);

        return "takenBooks";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/showBookingCards")
    public String showAllBookingCardsExceptOld(Model model) {
        List<BookCard> bookCards = bookCardService.findAllExceptReturned();

        model.addAttribute("bookCards", bookCards);

        return "bookCards";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/showBookingCards/old")
    public String showBookingCardsWithAllStatuses(Model model) {
        List<BookCard> bookCards = bookCardService.findAllBookCards();

        model.addAttribute("bookCards", bookCards);

        return "bookCards";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/approve/bookCard/{id}")
    public String approveBook(@PathVariable(name = "id") Long bookCardId, HttpServletRequest request) {
        userService.approveBookForUser(bookCardId);

        return getPreviousPageByRequest(request).orElse("/");
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/reject/bookCard/{id}")
    public String rejectBook(@PathVariable(name = "id") Long bookCardId, HttpServletRequest request) {
        userService.rejectTheBook(bookCardId);

        return getPreviousPageByRequest(request).orElse("/");
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/putBackIntoTheLibrary/{id}")
    public String returnedBook(@PathVariable(name = "id") Long bookCardId, HttpServletRequest request) {
        userService.returnedTheBook(bookCardId);

        return getPreviousPageByRequest(request).orElse("/");
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/delete/bookCard/{id}")
    public String deleteBookCard(@PathVariable(name = "id") Long bookCardId, HttpServletRequest request) {
        userService.deleteBookCard(bookCardId);

        return getPreviousPageByRequest(request).orElse("/");
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/showBookingCards/shouldApprove")
    public String showAllBookingCardsThatShouldBeApproved(Model model) {
        List<BookCard> bookCards = bookCardService.findAllBookCardsWithStatus(CardStatus.WAIT_FOR_APPROVE);

        model.addAttribute("bookCards", bookCards);

        return "bookCards";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/showBookingCards/shouldPutBack")
    public String showAllBookingCardsThatShouldBeBackedToTheLibrary(Model model) {
        List<BookCard> bookCards = bookCardService.findAllBookCardsWithStatus(CardStatus.APPROVED);

        model.addAttribute("bookCards", bookCards);

        return "bookCards";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/update/user/{id}")
    public String showUserUpdatePage(@PathVariable("id") Long id, Model model) {
        AppUser user = userService.findById(id);

        model.addAttribute("user", user);
        return "updateUserByAdmin";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/update/user/{id}")
    public String updateUser(Model model, @PathVariable("id") Long id, @ModelAttribute("user") @Valid UserModel userModel) {
        userService.updateUser(id, userModel);
        List<AppUser> appUsers = userService.showAllUsers();

        model.addAttribute("appUsers", appUsers);
        return "redirect:/admin/allUsers";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/create/user")
    public String createUser(Model model, @ModelAttribute("user") @Valid UserModel userModel) {
        model.addAttribute("userForm", new AppUser());

        return "createNewUser";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/create/user")
    public String createUser(@ModelAttribute("user") @Valid UserModel userModel, BindingResult result) {
        AppUser existing = userService.findByEmail(userModel.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            return "registration";
        }

        userService.save(userModel);

        return "redirect:/admin/allUsers";
    }


    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/delete/user/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
//        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
//        AppUser user = userService.findByEmail(currentUser);
//        List<AppUser> appUsers = userService.showAllUsers();

        userService.deleteUser(id);

//        model.addAttribute("appUser", user);
//        model.addAttribute("appUsers", appUsers);
        return getPreviousPageByRequest(request).orElse("/");
    }

    @GetMapping("/emailError")
    public String redirectToEmailErrorPage() {
        return "emailError";
    }

    @GetMapping("/update/password")
    public String redirectToUpdatePasswordPage() {
        return "updatePassword";
    }

    private Optional<String> getPreviousPageByRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> "redirect:" + requestUrl);
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


