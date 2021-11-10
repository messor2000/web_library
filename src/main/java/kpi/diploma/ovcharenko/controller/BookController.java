package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.service.book.IBookService;
import kpi.diploma.ovcharenko.service.book.IPagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Aleksandr Ovcharenko
 */
@Slf4j
@Controller
public class BookController {
    @Autowired
    IBookService bookService;
    @Autowired
    IPagingService pagingService;

    @RequestMapping(value = "/")
    public String getAllBook(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                             @RequestParam(value = "size", required = false, defaultValue = "20") int size, Model model) {
//        model.addAttribute("books", bookService.getAllBook());

        model.addAttribute("books", pagingService.getPage(pageNumber, size));
        return "library";
    }

    @RequestMapping(value = "/book/sort/asc")
    public String getAllBookByAsc(Model model) {
        model.addAttribute("books", bookService.getAllBooksByNameAsc());

        return "library";
    }

    @RequestMapping(value = "/book/{category}")
    public String getAllBookByCategory(Model model, @PathVariable("category") String category) {
        model.addAttribute("books", bookService.getAllBookByCategory(category));

        return "library";
    }

//    @RequestMapping(value = "/book/{bookName}")
//    public String getBookByName(Model model, @PathVariable("bookName") String bookName) {
//        model.addAttribute("books", bookService.getByName(bookName));
//
//        return "index";
//    }

    @RequestMapping(value = "/book")
    public String getBookByName(Model model, @RequestParam("bookName") String bookName) {
        model.addAttribute("books", bookService.getByName(bookName));

        return "library";
    }

    private static final String INDEX = "index";
}
