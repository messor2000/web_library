package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.card.CardStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCardRepository extends CrudRepository<BookCard, Long> {
    List<BookCard> findAll();

    List<BookCard> findAllByUserId(Long userId);

    List<BookCard> findAllByUserIdAndCardStatus(Long userId, CardStatus status);

    List<BookCard> findAllByCardStatus(CardStatus status);

    BookCard findBookCardById(Long bookCardId);

    void deleteBookCardById(Long id);
}

