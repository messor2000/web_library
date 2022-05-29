package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.exception.BookDoesntPresentException;
import kpi.diploma.ovcharenko.exception.ValidPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Slf4j
@ControllerAdvice
public class ErrorControllerAdvice extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private MessageSource messages;

    @Autowired
    private LocaleResolver localeResolver;

    @ExceptionHandler(BookDoesntPresentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String bookDoesntExistError(final BookDoesntPresentException throwable, final Model model) {
        log.trace("Exception during execution of application", throwable);
        model.addAttribute("error", "This book doesn't exist");
        return "error";
    }

    @ExceptionHandler(ValidPasswordException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String oldPasswordInvalidError(final ValidPasswordException throwable, final Model model) {
        log.trace("Exception during execution of application", throwable);
        model.addAttribute("error", "Your old password is invalid");
        return "error";
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        setDefaultFailureUrl("/login.html?error=true");

        super.onAuthenticationFailure(request, response, exception);

        Locale locale = localeResolver.resolveLocale(request);

        String errorMessage = messages.getMessage("message.badCredentials", null, locale);

        if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
            errorMessage = messages.getMessage("auth.message.disabled", null, locale);
        } else if (exception.getMessage().equalsIgnoreCase("User account has expired")) {
            errorMessage = messages.getMessage("auth.message.expired", null, locale);
        }

        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}
