package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.util.Paged;

/**
 * @author Aleksandr Ovcharenko
 */
public interface IPagingService {
    Paged<Book> getPage(int pageNumber, int size);
}
