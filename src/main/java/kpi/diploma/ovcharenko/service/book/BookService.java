package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface BookService {
    void deleteBookById(Long id);

    void updateBook(Book book, String category);

    Book findBookById(Long id);

    void addNewBook(Book book, String category);

    Page<Book> findBookByName(String name);

    Page<Book> getAllBooks(Pageable pageable);

    Page<Book> getSortingBooksByYear(Pageable pageable);

    Page<Book> getSortingBooksAlphabetical(Pageable pageable);

    Page<Book> getBookByCategory(Pageable pageable, String subject);

    Set<String> findAllCategories();

    Set<String> findBookCategories(Book book);
}
