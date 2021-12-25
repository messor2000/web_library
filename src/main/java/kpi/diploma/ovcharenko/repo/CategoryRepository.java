package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.BookCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends CrudRepository<BookCategory, Long> {
    @Query(value = "SELECT book_categories.category FROM web_library.book_categories", nativeQuery = true)
    Set<String> findAllCategories();

    void deleteAllByCategory(String category);

    List<BookCategory> findAllByCategoryContains(String category);
}
