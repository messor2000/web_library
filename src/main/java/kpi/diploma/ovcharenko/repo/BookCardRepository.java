package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.card.BookCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCardRepository extends CrudRepository<BookCard, Long> {
    List<BookCard> findAll();

    List<BookCard> findAllByUserId(Long userId);

    BookCard findBookCardByBookIdAndUserId(Long bookId, Long userId);
}
