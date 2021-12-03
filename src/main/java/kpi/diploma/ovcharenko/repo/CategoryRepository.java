package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.BookCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CategoryRepository extends CrudRepository<BookCategory, Long> {

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false " +
            "END FROM BookCategory b WHERE b.category = :category")
    boolean existsByCategory(@Param("category") String category);

    @Query(value = "SELECT book_categories.book_id FROM web_library.book_categories where category = :category", nativeQuery = true)
    List<Integer> findAllBookIdByCategory(@Param("category") String category);

    @Query(value = "SELECT book_categories.category FROM web_library.book_categories", nativeQuery = true)
    Set<String> findAllCategories();
}
