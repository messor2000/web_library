package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import kpi.diploma.ovcharenko.entity.book.BookTag;
import kpi.diploma.ovcharenko.entity.book.status.BookStatus;
import kpi.diploma.ovcharenko.entity.book.status.Status;
import kpi.diploma.ovcharenko.repo.BookCategoryRepository;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.repo.BookStatusRepository;
import kpi.diploma.ovcharenko.service.amazon.AmazonClient;
import kpi.diploma.ovcharenko.service.book.tags.BookTagService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class LibraryBookService implements BookService {

    private final BookRepository bookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookStatusRepository bookStatusRepository;
    private final BookTagService bookTagService;
    private final AmazonClient amazonClient;

    public LibraryBookService(BookRepository bookRepository, BookCategoryRepository bookCategoryRepository, AmazonClient amazonClient,
                              BookStatusRepository bookStatusRepository, BookTagService bookTagService) {
        this.bookRepository = bookRepository;
        this.bookCategoryRepository = bookCategoryRepository;
        this.bookStatusRepository = bookStatusRepository;
        this.amazonClient = amazonClient;
        this.bookTagService = bookTagService;
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateBook(Book book, String category, String tag) {
        List<BookStatus> bookStatuses = bookStatusRepository.findAllByBookId(book.getId());

        if (category != null && !category.equals("")) {
            if (!bookCategoryRepository.existsBookCategoryByCategoryAndBook(category, book)) {
                BookCategory bookCategory = new BookCategory(category);
                book.addCategory(bookCategory);
            }
        }

        if (tag != null && !tag.equals("")) {
            BookTag bookTag = bookTagService.findBookTagByTagName(tag).orElse(new BookTag(tag));
            bookTagService.saveBookTag(bookTag);
            Set<BookTag> allTags = bookTagService.findBookTagByBook(Collections.singleton(book));
            allTags.add(bookTag);
            book.setTags(allTags);
        }

        if (book.getAmount() > bookStatuses.size()) {
            int difference = book.getAmount() - bookStatuses.size();
            for (int i = 0; i < difference; i++) {
                BookStatus status = new BookStatus(Status.FREE);
                book.setStatus(status);
            }
        }

        if (book.getAmount() < bookStatuses.size()) {
            int difference = bookStatuses.size() - book.getAmount();
            for (int i = 0; i < difference; i++) {
                BookStatus status = bookStatuses.get(i);
                if (status.getStatus().equals(Status.FREE)) {
                    bookStatusRepository.delete(status);
                } else {
                    throw new RuntimeException("All books is used, can't delete");
                }
            }
        }

        bookRepository.save(book);
    }

    @Override
    public void addCoverToTheBook(MultipartFile file, Long bookId) {
        amazonClient.uploadImage(file, bookId);
    }

    @Override
    public void changeBookCover(MultipartFile file, Long bookId) {
        amazonClient.changeFile(file, bookId);
    }

    @Override
    public void deleteBookCover(Long bookId) {
        amazonClient.deleteFileFromS3("book/", String.valueOf(bookId));
    }

    @Override
    public void addBookPdf(MultipartFile file, Long bookId) {
        amazonClient.uploadBookPdf(file, bookId);
    }

    @Override
    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid retrievedBook Id:" + id));
    }

    @Override
    @Transactional
    public void addNewBook(Book book, String category, String tag) {
        for (int i = 0; i < book.getAmount(); i++) {
            BookStatus status = new BookStatus(Status.FREE);
            book.setStatus(status);
        }

        if (category != null && !category.equals("")) {
            BookCategory bookCategory = new BookCategory(category);
            book.addCategory(bookCategory);
        }

        if (tag != null && !tag.equals("")) {
            BookTag bookTag = bookTagService.findBookTagByTagName(tag).orElse(new BookTag(tag));
            bookTag.getBooks().add(book);
            book.getTags().add(bookTag);
        }

        bookRepository.save(book);
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> getBookByCategory(Pageable pageable, String category) {
        return bookRepository.findByCategoryContains(category, pageable);
    }

    @Override
    public Set<String> findAllCategories() {
        Set<String> stringBookCategories = new HashSet<>();
        List<BookCategory> bookCategories = (List<BookCategory>) bookCategoryRepository.findAll();
        for (BookCategory bookCategory: bookCategories) {
            stringBookCategories.add(bookCategory.getCategory());
        }

        return stringBookCategories;
    }

    @Override
    public Set<String> findBookCategories(Book book) {
        Set<BookCategory> categories = book.getCategories();

        Set<String> bookCategories = new HashSet<>();

        for (BookCategory category : categories) {
            bookCategories.add(category.getCategory());
        }

        return bookCategories;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id, String category) {
        Book book = findBookById(id);
        BookCategory bookCategory = bookCategoryRepository.findByCategoryAndBook(category, book);
        book.removeCategory(bookCategory);

        bookCategoryRepository.deleteById(bookCategory.getId());
        bookRepository.save(book);
    }

    @Override
    public Page<Book> findByKeyWord(String search, Pageable pageable) {
        if (NumberUtils.isDigits(search)) {
            return bookRepository.findByYearContaining(Integer.parseInt(search), pageable);
        }

//        Page<Book> books = bookRepository.findByBookCardsContaining(search, pageable);

        return bookRepository.findByBookNameOrAuthorOrYear(search, pageable);
    }

    @Override
    public List<BookStatus> getAllBooksStatus(Long bookId) {
        return bookStatusRepository.findAllByBookId(bookId);
    }

    @Override
    public void deleteTagFromTheBook(Long bookId, String tag) {
        Book book = findBookById(bookId);
        Set<BookTag> allTags = bookTagService.findBookTagByBook(Collections.singleton(book));

        BookTag bookTag = bookTagService.findBookTagByTagName(tag).get();

        allTags.remove(bookTag);

        book.setTags(allTags);

        bookRepository.save(book);
    }

    private void readPdf(HttpServletResponse response, String stringFile) {
        response.reset();
        response.setContentType("application/pdf");
        try {
            File file = new File(stringFile);
            FileInputStream fileInputStream = new FileInputStream(file);
            OutputStream outputStream = response.getOutputStream();
            IOUtils.write(IOUtils.toByteArray(fileInputStream), outputStream);
            response.setHeader("Content-Disposition",
                    "inline; filename= file");
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


