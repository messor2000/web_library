package kpi.diploma.ovcharenko.controller;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LocaleController {

    private final MessageSource messageSource;

    public LocaleController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping(value = "/international")
    public String getMealsPage(@RequestParam(name = "lang") String lang, HttpSession session, HttpServletRequest request) {
        session.setAttribute("local", lang);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
}

