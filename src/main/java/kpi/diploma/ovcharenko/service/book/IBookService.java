package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.util.Paged;

import java.util.List;

/**
 * @author Aleksandr Ovcharenko
 */
public interface IBookService {
    List<Book> getAllBook();

    List<Book> getAllBooksByNameAsc();

    List<Book> getAllBookByCategory(String subject);

    Book getByName(String name);
}
