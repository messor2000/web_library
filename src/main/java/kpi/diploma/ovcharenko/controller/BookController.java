package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.service.book.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Controller
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(value = "/")
    public String getAllBooks(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(20);

        Page<Book> bookPage = bookService.getAllBooks(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("books", bookPage);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "library";
    }

    @GetMapping(value = "/published")
    public String getSortingBooksByYear(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(20);

        Page<Book> bookPage = bookService.getSortingBooksByYear(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("books", bookPage);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "library";
    }

    @GetMapping(value = "/alphabetical")
    public String getSortingBooksAlphabetical(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(20);

        Page<Book> bookPage = bookService.getSortingBooksAlphabetical(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("books", bookPage);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "library";
    }

    @GetMapping(value = "/{category}")
    public String getBooksByCategory(Model model, @PathVariable("category") String category, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(20);

        Page<Book> bookPage = bookService.getBookByCategory(PageRequest.of(currentPage - 1, pageSize), category);

        model.addAttribute("books", bookPage);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "library";
    }

    @GetMapping(value = "/find")
    public String findBookByName(@RequestParam(value = "bookName", required = false) String name, Model model) {
        Page<Book> book = bookService.findBookByName(name);

        model.addAttribute("books", book);

        return "library";
    }

    @GetMapping(value = "/delete/{id}")
    public String deleteBookByName(@PathVariable("id") Long id) {
        bookService.deleteBookById(id);

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.findBookById(id);

        model.addAttribute("book", book);
        return "editBook";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid Book book, BindingResult result, HttpServletRequest request) {

        String query = request.getRequestURI();
        System.out.println(query);

        if (result.hasErrors()) {
            book.setId(id);
            return "editBook";
        }

        bookService.updateBook(book);

        return "redirect:/";
    }
}
