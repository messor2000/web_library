package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.service.book.IBookService;
import kpi.diploma.ovcharenko.service.book.IPagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Aleksandr Ovcharenko
 */
@Controller
@RequestMapping(value = "/table")
public class TableBookController {
    @Autowired
    IBookService bookService;
    @Autowired
    IPagingService pagingService;

    @GetMapping(value = "/books/all")
    public String posts(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                        @RequestParam(value = "size", required = false, defaultValue = "20") int size, Model model) {
        model.addAttribute("books", pagingService.getPage(pageNumber, size));
        return "tableLibrary";
    }
}
