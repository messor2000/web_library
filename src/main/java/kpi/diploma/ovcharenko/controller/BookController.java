package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.service.book.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Controller
public class BookController {

    private static final int FIRST_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(value = "/")
    public String getAllBooks(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(FIRST_PAGE);
        int pageSize = size.orElse(DEFAULT_PAGE_SIZE);

        Page<Book> bookPage = bookService.getAllBooks(PageRequest.of(currentPage - 1, pageSize));
        Set<String> categories = bookService.findAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", currentPage);
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
        int currentPage = page.orElse(FIRST_PAGE);
        int pageSize = size.orElse(DEFAULT_PAGE_SIZE);

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
        int currentPage = page.orElse(FIRST_PAGE);
        int pageSize = size.orElse(DEFAULT_PAGE_SIZE);

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
    public String getBooksByCategory(Model model, @PathVariable("category") String category,
                                     @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(FIRST_PAGE);
        int pageSize = size.orElse(DEFAULT_PAGE_SIZE);

        Page<Book> bookPage = bookService.getBookByCategory(PageRequest.of(currentPage - 1, pageSize), category);
        Set<String> categories = bookService.findAllCategories();

        model.addAttribute("books", bookPage);
        model.addAttribute("categories", categories);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "library";
    }


    @GetMapping(value = "/find/{id}")
    public String findBookByName(@PathVariable(value = "id", required = false) Long id, Model model) {
        Book book = bookService.findBookById(id);
        Set<String> bookCategories = bookService.findBookCategories(book);

        model.addAttribute("bookCategories", bookCategories);
        model.addAttribute("book", book);

        return "bookInfo";
    }

    @GetMapping(value = "/delete/{id}")
    public String deleteBookByName(@PathVariable("id") Long id) {
        bookService.deleteBookById(id);

        return "redirect:/";
    }

    @GetMapping("/add")
    public String showAddForm(Book book, Model model) {
        Set<String> categories = bookService.findAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("book", book);
        return "addBook";
    }

    @PostMapping("/add")
    public String addNewBook(@Valid Book book, BindingResult result, @RequestParam(value = "image", required = false) MultipartFile file,
                             @RequestParam(value = "category") String category) {
        if (result.hasErrors()) {
            return "addBook";
        }

        bookService.addNewBook(book, category, file);

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.findBookById(id);
        Set<String> categories = bookService.findAllCategories();
        Set<String> bookCategories = bookService.findBookCategories(book);

        model.addAttribute("categories", categories);
        model.addAttribute("bookCategories", bookCategories);
        model.addAttribute("book", book);
        return "editBook";
    }

    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable("id") long id, @Valid Book book, @RequestParam(value = "category") String category,
                             @RequestParam(value = "image", required = false) MultipartFile multipartFile, BindingResult result) {
        if (result.hasErrors()) {
            book.setId(id);
            return "editBook";
        }

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        bookService.updateBook(book, category, multipartFile);

        return "redirect:/";
    }

    @GetMapping("/changeCategories")
    public String changeCategories(Model model) {
        Set<String> categories = bookService.findAllCategories();

        model.addAttribute("categories", categories);
        return "changeBookCategories";
    }

    @PostMapping("/changeCategory/{newCategory}")
    public String changeCategory(@RequestParam("category") String category, @PathVariable("newCategory") String newCategory) {
        bookService.updateCategory(category, newCategory);

        return "redirect:/";
    }

    @PostMapping("/deleteCategory/{category}")
    public String deleteCategory(@PathVariable("category") String category) {
        bookService.deleteCategory(category);

        return "redirect:/";
    }
}
