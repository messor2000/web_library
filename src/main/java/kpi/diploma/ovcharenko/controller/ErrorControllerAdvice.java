package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.exception.BookDoesntPresentException;
import kpi.diploma.ovcharenko.exception.ValidPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ErrorControllerAdvice {

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
}
