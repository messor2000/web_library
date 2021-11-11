package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.entity.BookModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    void deleteBookByName(String name);

    void updateBook(Book book);

    Book findBookById(Long id);

    Page<Book> getAllBooks(Pageable pageable);

    Page<Book> getSortingBooksByYear(Pageable pageable);

    Page<Book> getSortingBooksAlphabetical(Pageable pageable);

    Page<Book> getBookByCategory(Pageable pageable, String subject);
}
