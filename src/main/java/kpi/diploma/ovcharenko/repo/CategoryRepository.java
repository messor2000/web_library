package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface CategoryRepository extends CrudRepository<BookCategory, Long> {
    BookCategory findByCategoryAndBook(String category, Book book);
}
