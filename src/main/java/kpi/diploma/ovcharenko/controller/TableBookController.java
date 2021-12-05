package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.service.book.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping(value = "/table")
public class TableBookController {

    private static final int FIRST_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final BookService bookService;

    @Autowired
    public TableBookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(value = "/books/all")
    public String getAllBooks(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(FIRST_PAGE);
        int pageSize = size.orElse(DEFAULT_PAGE_SIZE);

        Page<Book> bookPage = bookService.getAllBooks(PageRequest.of(currentPage - 1, pageSize));
        Set<String> categories = bookService.findAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("books", bookPage);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "tableLibrary";
    }

    @GetMapping(value = "/find")
    public String findBookByName(@RequestParam(value = "bookName", required = false) String name, Model model) {
        Page<Book> book = bookService.findBookByName(name);

        model.addAttribute("books", book);

        return "tableLibrary";
    }
}
