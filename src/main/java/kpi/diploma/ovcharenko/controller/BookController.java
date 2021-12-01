package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.service.book.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
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

        model.addAttribute("books", bookPage);
        model.addAttribute("category", category);

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
//        Page<Book> book = bookService.findBookByName(name);

        Book book = bookService.findBookById(id);

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
        model.addAttribute("book", book);
        return "addBook";
    }

    @PostMapping("/add")
    public String addNewBook(@Valid Book book, BindingResult result, @RequestParam("image") MultipartFile file) {
        if (result.hasErrors()) {
            return "addBook";
        }

        try {

            byte[] image = file.getBytes();
            book.setImage(image);
            bookService.addNewBook(book);

        } catch (Exception e) {
            log.error("ERROR", e);
            return "error";
        }

        bookService.addNewBook(book);

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.findBookById(id);

        model.addAttribute("book", book);
        return "editBook";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid Book book, BindingResult result) {
        if (result.hasErrors()) {
            book.setId(id);
            return "editBook";
        }

        bookService.updateBook(book);

        return "redirect:/";
    }

//    @PostMapping("/image/saveImageDetails")
//    public @ResponseBody ResponseEntity<?> createProduct(@RequestParam("name") String name,
//                                                         @RequestParam("price") double price, @RequestParam("description") String description, Model model, HttpServletRequest request
//            ,final @RequestParam("image") MultipartFile file) {
//        try {
//            //String uploadDirectory = System.getProperty("user.dir") + uploadFolder;
//            String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
//            String fileName = file.getOriginalFilename();
//            String filePath = Paths.get(uploadDirectory, fileName).toString();
//
//            if (fileName == null || fileName.contains("..")) {
//
//            }
//
//            try {
//                File dir = new File(uploadDirectory);
//                if (!dir.exists()) {
//                    log.info("Folder Created");
//                    dir.mkdirs();
//                }
//                // Save the file locally
//                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
//                stream.write(file.getBytes());
//                stream.close();
//            } catch (Exception e) {
//                log.info("in catch");
//                e.printStackTrace();
//            }
//            byte[] imageData = file.getBytes();
//            ImageGallery imageGallery = new ImageGallery();
//            imageGallery.setName(names[0]);
//            imageGallery.setImage(imageData);
//            imageGallery.setPrice(price);
//            imageGallery.setDescription(descriptions[0]);
//            imageGallery.setCreateDate(createDate);
//            imageGalleryService.saveImage(imageGallery);
//            log.info("HttpStatus===" + new ResponseEntity<>(HttpStatus.OK));
//            return new ResponseEntity<>("Product Saved With File - " + fileName, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("Exception: " + e);
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }

}
