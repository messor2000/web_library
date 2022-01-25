package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.service.book.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
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

    public BookController(BookService bookService) {
        this.bookService = bookService;
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
    public @ResponseBody ResponseEntity<?> createProduct(@PathVariable("id") long id, @Valid Book book,
                                    @RequestParam(value = "category") String category, Model model,
                                    HttpServletRequest request, final @RequestParam("image") MultipartFile file) {
        try {
            //String uploadDirectory = System.getProperty("user.dir") + uploadFolder;
            String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
            log.info("uploadDirectory:: " + uploadDirectory);
            String fileName = file.getOriginalFilename();
            String filePath = Paths.get(uploadDirectory, fileName).toString();
            log.info("FileName: " + file.getOriginalFilename());
            if (fileName == null || fileName.contains("..")) {
                model.addAttribute("invalid", "Sorry! Filename contains invalid path sequence \" + fileName");
                return new ResponseEntity<>("Sorry! Filename contains invalid path sequence " + fileName, HttpStatus.BAD_REQUEST);
            }
            File dir = new File(uploadDirectory);
            if (!dir.exists()) {
                log.info("Folder Created");
                dir.mkdirs();
            }
            // Save the file locally
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            stream.write(file.getBytes());
            stream.close();
            byte[] imageData = file.getBytes();

            bookService.updateBook(book, category, imageData);

            log.info("HttpStatus===" + new ResponseEntity<>(HttpStatus.OK));
            return new ResponseEntity<>("Product Saved With File - " + fileName, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception: " + e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/book/cover/{id}")
    @ResponseBody
    public void showImage(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        log.info("Id :: " + id);
        Book book = bookService.findBookById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(book.getImage());
        response.getOutputStream().close();
    }

//    @PostMapping(value = "/update/{id}", consumes = {"multipart/form-data"})
//    public String updateBook(@PathVariable("id") long id, @Valid Book book, @RequestParam(value = "category") String category,
//                             BindingResult result, @RequestParam(value = "image", required = false) MultipartFile multipartFile) {
//        if (result.hasErrors()) {
//            book.setId(id);
//            return "editBook";
//        }
//
//        bookService.updateBook(book, category, multipartFile);
//
//        return "redirect:/";
//    }

    @PostMapping("/deleteCategory/{id}")
    public String deleteBookCategory(@PathVariable("id") long id, @RequestParam(value = "category") String category, RedirectAttributes redirectAttributes) {
        bookService.deleteCategory(id, category);

        redirectAttributes.addAttribute("bookId", id);
        return "redirect:/edit/{bookId}";
    }
}
