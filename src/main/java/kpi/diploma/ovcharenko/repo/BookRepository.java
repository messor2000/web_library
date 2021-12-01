package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);

    Page<Book> findBySubjectContains(Pageable pageable, String subject);

    Page<Book> findByBookName(String bookName, Pageable pageable);

    @Query(value = "SELECT subjects FROM web_library.books", nativeQuery = true)
    Set<String> findAllCategories();
}
