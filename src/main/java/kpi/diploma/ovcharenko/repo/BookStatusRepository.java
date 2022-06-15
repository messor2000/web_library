package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.BookStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookStatusRepository extends CrudRepository<BookStatus, Long> {
    List<BookStatus> findAllByBookId(Long id);

    void deleteBookStatusesByBookId(Long bookId);
}
