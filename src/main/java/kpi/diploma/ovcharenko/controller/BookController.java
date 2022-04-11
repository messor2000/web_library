package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookModel;
import kpi.diploma.ovcharenko.service.amazon.AmazonClient;
import kpi.diploma.ovcharenko.service.book.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Controller
public class BookController {

    @Value("${uploadDir}")
    private String uploadFolder;

    private static final int FIRST_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final BookService bookService;
    private final AmazonClient amazonClient;

    public BookController(BookService bookService, AmazonClient amazonClient) {
        this.bookService = bookService;
        this.amazonClient = amazonClient;
    }

    @GetMapping("/")
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

    @GetMapping(value = "/{category}")
    public String getBooksByCategory(Model model, @PathVariable("category") String category,
                                     @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size,
                                        RedirectAttributes redirectAttributes) {
        int currentPage = page.orElse(FIRST_PAGE);
        int pageSize = size.orElse(DEFAULT_PAGE_SIZE);

        Page<Book> bookPage = bookService.getBookByCategory(PageRequest.of(currentPage - 1, pageSize), category);
        Set<String> categories = bookService.findAllCategories();

        if (!categories.contains(category)) {
            redirectAttributes.addFlashAttribute("message",
                    "Such category doesn't exist" + category + "!");
        }

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
    public String findBookById(@PathVariable(value = "id", required = false) Long id, Model model) {
        Book book = bookService.findBookById(id);
        Set<String> bookCategories = bookService.findBookCategories(book);

        model.addAttribute("bookCategories", bookCategories);
        model.addAttribute("book", book);

        return "bookInfo";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/book/delete/{id}")
    public String deleteBookById(@PathVariable("id") Long id) {
        bookService.deleteBookById(id);

        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/add")
    public String showAddForm(BookModel bookModel, Model model) {
        Set<String> categories = bookService.findAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("bookModel", bookModel);
        return "addBook";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public String addNewBook(@Valid Book book, BindingResult result, @RequestParam(value = "category") String category,
                             @RequestParam("image") MultipartFile multipartFile) {
        if (result.hasErrors()) {
            return "addBook";
        }

        bookService.addNewBook(book, category, multipartFile);

        return "redirect:/";
    }

    @GetMapping("/book/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.findBookById(id);
        Set<String> categories = bookService.findAllCategories();
        Set<String> bookCategories = bookService.findBookCategories(book);

        model.addAttribute("categories", categories);
        model.addAttribute("bookCategories", bookCategories);
        model.addAttribute("book", book);
        return "editBook";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/book/update/{id}", consumes = {"multipart/form-data"})
    public String updateBook(@PathVariable("id") long id, @Valid Book book,
                             @RequestParam(value = "category") String category, BindingResult result,
                             @RequestParam("image") MultipartFile multipartFile) {
        if (result.hasErrors()) {
            book.setId(id);
            return "editBook";
        }

        bookService.updateBook(book, category, multipartFile);

        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/deleteCategory/{id}")
    public String deleteBookCategory(@PathVariable("id") long id, @RequestParam(value = "category") String category,
                                     RedirectAttributes redirectAttributes) {
        bookService.deleteCategory(id, category);

        redirectAttributes.addAttribute("bookId", id);
        return "redirect:/edit/{bookId}";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/book/addCover/{id}")
    public String addBookCover(@PathVariable("id") long id, @RequestParam("image") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("bookId", id);
        amazonClient.upload(multipartFile);

        return "redirect:/edit/{bookId}";
    }
}

