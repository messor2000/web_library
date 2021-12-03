package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);

    Page<Book> findByBookName(String bookName, Pageable pageable);

    Page<Book> findById(Long id, Pageable pageable);

    @Query(value = "SELECT b from Book b inner join BookCategory bc on bc.book.id = b.id where bc.category = :category")
    Page<Book> findByCategoryContains(@Param("category") String category, Pageable pageable);
}
