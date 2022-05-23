package kpi.diploma.ovcharenko.service.book.cards;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.card.CardStatus;

import java.util.List;

public interface BookCardService {

    void saveBookCard(BookCard bookCard);
    List<BookCard> findAllBookCards();

    List<BookCard> findAllExceptReturned();

    List<BookCard> findAllUserBookCards(Long userId);

    List<BookCard> findAllUserBookCardsAndStatus(Long userId, CardStatus status);

    List<BookCard> findAllBookCardsWithStatus(CardStatus status);

    BookCard findBookCardById(Long bookCardId);

    void deleteBookCardByBookCardId(Long bookCardId);

    void deleteBookCardByBookId(Long bookId);

    void deleteBookCardByUserId(Long userId);
}
