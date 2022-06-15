package kpi.diploma.ovcharenko.service.book.cards;

import kpi.diploma.ovcharenko.entity.card.BookCard;

import java.util.List;

public interface BookCardService {
    void saveBookCard(BookCard bookCard);

    List<BookCard> findAllBookCards();

    List<BookCard> findAllUserBookCards(Long userId);

    List<BookCard> findAllBookCardsWithStatus(String status);

    BookCard findBookCardById(Long bookCardId);

    void deleteBookCardByBookCardId(Long bookCardId);

    void deleteBookCardByUserId(Long userId);

    void deleteAllBookCardsByBookId(Long bookId);
}
