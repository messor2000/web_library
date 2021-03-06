package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface BookService {
    void deleteBookById(Long id);

    Book findBookById(Long id);

    void addNewBook(Book book, String category, String tag);

    void updateBook(Book book, String category, String tag);

    void addCoverToTheBook(MultipartFile file, Long bookId);

    void changeBookCover(MultipartFile file, Long bookId);

    void addBookPdf(MultipartFile file, Long bookId);

    Page<Book> getAllBooks(Pageable pageable);

    Page<Book> getBookByCategory(Pageable pageable, String subject);

    Set<String> findAllCategories();

    Set<String> findBookCategories(Book book);

    void deleteCategory(Long bookId, String category);

    Page<Book> findByKeyWord(String search, Pageable pageable);

    List<BookStatus> getAllBooksStatus(Long bookId);

    void deleteTagFromTheBook(Long bookId, String tag);
}
