package kpi.diploma.ovcharenko.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Aleksandr Ovcharenko
 */
@Controller
public class InfoController {

    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
}
