package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Aleksandr Ovcharenko
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
