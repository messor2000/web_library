package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookModel;
import kpi.diploma.ovcharenko.entity.book.status.BookStatus;
import kpi.diploma.ovcharenko.entity.book.status.Status;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.book.tags.BookTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final BookTagService bookTagService;

    public BookController(BookService bookService, BookTagService bookTagService) {
        this.bookService = bookService;
        this.bookTagService = bookTagService;
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

    @GetMapping(value = "/category")
    public String getBooksByCategory(Model model, @RequestParam("category") String category,
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
        List<BookStatus> statuses = bookService.getAllBooksStatus(id);
        Set<String> bookTags = bookTagService.findBookTagsByBookId(id);
        int statusFree = 0;
        int statusBooked = 0;
        int statusTaken = 0;

        for (BookStatus status : statuses) {
            if (status.getStatus().equals(Status.FREE)) {
                statusFree++;
            }
            if (status.getStatus().equals(Status.BOOKED)) {
                statusBooked++;
            }
            if (status.getStatus().equals(Status.TAKEN)) {
                statusTaken++;
            }
        }

        model.addAttribute("bookCategories", bookCategories);
        model.addAttribute("book", book);
        model.addAttribute("bookTags", bookTags);
        model.addAttribute("statuses", statuses);
        model.addAttribute("statusFree", statusFree);
        model.addAttribute("statusBooked", statusBooked);
        model.addAttribute("statusTaken", statusTaken);

        log.info(String.valueOf(statuses));

        return "bookInfo";
    }

    @GetMapping(value = "/find")
    public String findBookByKeyword(Model model, @RequestParam("search") String search,
                                    @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(FIRST_PAGE);
        int pageSize = size.orElse(DEFAULT_PAGE_SIZE);

        Page<Book> books = bookService.findByKeyWord(search, PageRequest.of(currentPage - 1, pageSize));
        Set<String> categories = bookService.findAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("books", books);

        return "library";
    }

    @GetMapping(value = "/download/book/{id}")
    public String downloadBook(@PathVariable("id") Long id) {
        bookService.downloadPdf(id);

        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/admin/delete/{id}")
    public String deleteBookById(@PathVariable("id") Long id) {
        bookService.deleteBookById(id);

        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/add")
    public String showAddForm(BookModel bookModel, Model model) {
        Set<String> categories = bookService.findAllCategories();
        Set<String> bookTags = bookTagService.findAllTagNames();

        model.addAttribute("categories", categories);
        model.addAttribute("bookModel", bookModel);
        model.addAttribute("bookTags", bookTags);
        return "addBook";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/book/add")
    public String addNewBook(@Valid Book book, BindingResult result, @RequestParam(value = "category") String category,
                             @RequestParam(value = "bookTag") String bookTag,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) {
        if (result.hasErrors()) {
            return "addBook";
        }

        bookService.addNewBook(book, category, bookTag);
        if (!imageFile.isEmpty()) {
            bookService.changeBookCover(imageFile, book.getId());
        }
        if (!pdfFile.isEmpty()) {
            bookService.addBookPdf(pdfFile, book.getId());
        }

        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.findBookById(id);
        Set<String> categories = bookService.findAllCategories();
        Set<String> tags = bookTagService.findAllTagNames();
        Set<String> bookCategories = bookService.findBookCategories(book);
        Set<String> bookTags = bookTagService.findBookTagsByBookId(id);

        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);
        model.addAttribute("bookCategories", bookCategories);
        model.addAttribute("bookTags", bookTags);
        model.addAttribute("book", book);
        return "editBook";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/book/update/{id}", consumes = {"multipart/form-data"})
    public String updateBook(@PathVariable("id") long id, @Valid Book book, @RequestParam(value = "bookTag") String bookTag,
                             @RequestParam(value = "category") String category, BindingResult result,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile) {
        if (result.hasErrors()) {
            book.setId(id);
            return "editBook";
        }

        bookService.updateBook(book, category, bookTag);
        if (!imageFile.isEmpty()) {
            bookService.changeBookCover(imageFile, id);
        }
        if (!pdfFile.isEmpty()) {
            bookService.addBookPdf(pdfFile, id);
        }

        return "redirect:/";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/book/deleteCategory/{id}")
    public String deleteBookCategory(@PathVariable("id") long id, @RequestParam(value = "category") String category) {
        bookService.deleteCategory(id, category);

        return "redirect:/";
    }
}

