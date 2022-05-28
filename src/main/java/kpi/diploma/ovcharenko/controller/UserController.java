package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.card.CardStatus;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.entity.user.VerificationToken;
import kpi.diploma.ovcharenko.exception.ValidPasswordException;
import kpi.diploma.ovcharenko.service.activation.OnRegistrationCompleteEvent;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.book.cards.BookCardService;
import kpi.diploma.ovcharenko.service.user.LibrarySecurityService;
import kpi.diploma.ovcharenko.service.user.SecurityService;
import kpi.diploma.ovcharenko.service.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Controller
public class UserController {

    private final UserService userService;

    private final BookService bookService;
    private final BookCardService bookCardService;
    private final SecurityService securityService;
    private final MessageSource messages;
    private final JavaMailSender mailSender;
    private final ApplicationEventPublisher eventPublisher;

    public UserController(UserService userService, LibrarySecurityService securityService, MessageSource messages,
                          JavaMailSender mailSender, BookCardService bookCardService, ApplicationEventPublisher eventPublisher,
                          BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
        this.securityService = securityService;
        this.messages = messages;
        this.mailSender = mailSender;
        this.bookCardService = bookCardService;
        this.eventPublisher = eventPublisher;
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
    public String registerUserAccount(@ModelAttribute("user") @Valid UserModel userModel, BindingResult result, HttpServletRequest request) {
        AppUser existing = userService.findByEmail(userModel.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        if (result.hasErrors()) {
            return "registration";
        }

        userService.save(userModel);
        final AppUser user = userService.findByEmail(userModel.getEmail());

        final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));

        return "regSuccessfully";
    }

    @GetMapping("/registration/confirm")
    @Transactional
    public String confirmRegistration(final HttpServletRequest request, final Model model, @RequestParam("token") final String token) {
        final Locale locale = request.getLocale();

        final VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            final String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser";
        }

        final AppUser user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
            model.addAttribute("expired", true);
            model.addAttribute("token", token);
            return "redirect:/badUser";
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));

        return "accountActivated";
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

    @GetMapping("/user/reset/password")
    public String showChangePasswordPage(@RequestParam("token") String token, RedirectAttributes redirectAttributes) {
        String result = securityService.validatePasswordResetToken(token);
        if (result != null) {
            return "redirect:/login";
        } else {
            redirectAttributes.addAttribute("token", token);
            return "redirect:/update/password";
        }
    }

    @PostMapping("/user/save/password")
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

        model.addAttribute("appUser", user);
        model.addAttribute("bookCards", bookCards);
        model.addAttribute("bookCardsWithStatus", bookCards);

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
    public String updateUserProfile(Model model, @ModelAttribute("user") @Valid UserModel userModel,
                                    @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser user = userService.findByEmail(currentUser);
        userService.updateUser(user.getId(), userModel);
        List<BookCard> bookCards = bookCardService.findAllUserBookCards(user.getId());
        List<AppUser> appUsers = userService.showAllUsers();

        if (!imageFile.isEmpty()) {
            userService.changePhotoImage(imageFile, user.getEmail());
        }

        model.addAttribute("appUser", user);
        model.addAttribute("bookCards", bookCards);
        model.addAttribute("appUsers", appUsers);

        return "accountActivated";
    }

    @GetMapping("/user/change/password")
    public String showChangePasswordPage() {
        return "changePassword";
    }

    @PostMapping("/user/update/password")
    public String changeUserPassword(@RequestParam("password") String password,
                                     @RequestParam("oldpassword") String oldPassword) throws ValidPasswordException {
        AppUser user = userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!userService.checkIfValidOldPassword(user, oldPassword)) {
            throw new ValidPasswordException("Old password is invalid");
        }
        userService.changeUserPassword(user, password);
        return "redirect:/login";
    }

    @PostMapping("/takeBook/{id}")
    public String takeBook(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        Book book = bookService.findBookById(id);
        if (book.getAmount() == 0) {
            return "bookTakenError";
        } else {
            userService.bookedBook(id, currentUser);
        }

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
    public String returnBook(@PathVariable(name = "id") Long bookCardId, HttpServletRequest request) {
        userService.returnTheBook(bookCardId);

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

        userService.createNewUserByAdmin(userModel);

        return "redirect:/admin/allUsers";
    }


    @Secured("ROLE_ADMIN")
    @PostMapping("/admin/delete/user/{id}")
    public String deleteUser(@PathVariable("id") Long id, HttpServletRequest request) {
        userService.deleteUser(id);

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

    @GetMapping("/forgetPassword")
    public String showForgetPasswordPage() {
        return "forgetPassword";
    }

    private Optional<String> getPreviousPageByRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> "redirect:" + requestUrl);
    }

    private SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, AppUser user) {
        String url = contextPath + "/user/reset/password?token=" + token;
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


