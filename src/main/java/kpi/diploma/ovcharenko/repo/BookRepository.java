package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);

    Page<Book> findBySubjectContains(Pageable pageable, String subject);

    void deleteByBookName(String bookName);

    Book findByBookName(String bookName);
}
