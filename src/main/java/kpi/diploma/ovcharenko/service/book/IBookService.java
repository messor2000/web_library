package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.Book;

import java.util.List;

/**
 * @author Aleksandr Ovcharenko
 */
public interface IBookService {
    List<Book> getAllBooksByNameAsc();

    List<Book> getAllBookByCategory(String subject);

    Book getByName(String name);

    void deleteBookByName(String name);
}
