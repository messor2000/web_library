package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.card.CardStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCardRepository extends CrudRepository<BookCard, Long> {
    List<BookCard> findAll();

    List<BookCard> findAllByUserId(Long userId);

    List<BookCard> findAllByCardStatus(CardStatus status);

    BookCard findBookCardById(Long bookCardId);

    @Modifying
    @Query("delete from BookCard bc where bc.book.id = :id")
    void deleteBookCardsByBookId(Long id);

    @Modifying
    @Query("delete from BookCard bc where bc.user.id = :id")
    void deleteBookCardsByUserId(Long id);

    @Modifying
    @Query("delete from BookCard bc where bc.id = :id")
    void deleteBookCardByCardId(Long id);
}

