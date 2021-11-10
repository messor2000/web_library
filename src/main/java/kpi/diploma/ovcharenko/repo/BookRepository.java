package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Aleksandr Ovcharenko
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAll(Sort sort);

    List<Book> findAllBySubject(String subject);

    Book getByBookName(String bookName);

    void deleteByBookName(String bookName);
}
