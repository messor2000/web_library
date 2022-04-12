package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface BookService {
    void deleteBookById(Long id);

    Book findBookById(Long id);

    void addNewBook(Book book, String category, MultipartFile file);

    void updateBook(Book book, String category, MultipartFile file);

    Page<Book> findBookByName(String name);

    Page<Book> getAllBooks(Pageable pageable);

    Page<Book> getBookByCategory(Pageable pageable, String subject);

    Set<String> findAllCategories();

    Set<String> findBookCategories(Book book);

    void deleteCategory(Long id, String category);

    List<Book> getAllBooksThatTaken(String email);
}
