package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.card.CardStatus;

import java.util.List;

public interface BookCardService {
    List<BookCard> findAllBookCards();

    List<BookCard> findAllExceptReturned();

    List<BookCard> findAllUserBookCards(Long userId);

    List<BookCard> findAllUserBookCardsAndStatus(Long userId, CardStatus status);

    List<BookCard> findAllBookCardsWithStatus(CardStatus status);

    void deleteBookCard(Long bookCardId);
}
