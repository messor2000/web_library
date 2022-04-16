package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);

    @Query(value = "SELECT distinct b from Book b inner join BookCategory bc on bc.book.id = b.id where bc.category = :category")
    Page<Book> findByCategoryContains(@Param("category") String category, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.users u WHERE u.email LIKE %?1%")
    List<Book> findBooksThatTakenByUser(String keyword);

    @Query("select b from Book b where lower(b.bookName) like lower(concat('%', :search, '%')) " +
            "or lower(b.author) like lower(concat('%', :search, '%')) or lower(b.year) like lower(concat('%', :search, '%'))")
    Page<Book> findByBookNameOrAuthorOrYear(@Param("search") String search, Pageable pageable);
}
