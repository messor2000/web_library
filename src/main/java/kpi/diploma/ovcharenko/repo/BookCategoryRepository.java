package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import org.springframework.data.repository.CrudRepository;

public interface BookCategoryRepository extends CrudRepository<BookCategory, Long> {
    BookCategory findByCategoryAndBook(String category, Book book);

    boolean existsBookCategoryByCategoryAndBook(String category, Book book);
}
