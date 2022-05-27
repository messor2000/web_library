package kpi.diploma.ovcharenko.service.book.cards;

import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.card.CardStatus;
import kpi.diploma.ovcharenko.repo.BookCardRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LibraryBookCardService implements BookCardService {
    private final BookCardRepository bookCardRepository;

    public LibraryBookCardService(BookCardRepository bookCardRepository) {
        this.bookCardRepository = bookCardRepository;
    }

    @Override
    public void saveBookCard(BookCard bookCard) {
        bookCardRepository.save(bookCard);
    }

    @Override
    public List<BookCard> findAllBookCards() {
        return bookCardRepository.findAll();
    }

    @Override
    public List<BookCard> findAllExceptReturned() {
        List<BookCard> bookCards = new ArrayList<>();

        bookCards.addAll(bookCardRepository.findAllByCardStatus(CardStatus.WAIT_FOR_APPROVE));
        bookCards.addAll(bookCardRepository.findAllByCardStatus(CardStatus.APPROVED));

        return bookCards;
    }

    @Override
    public List<BookCard> findAllUserBookCards(Long userId) {
        return bookCardRepository.findAllByUserId(userId);
    }

    @Override
    public List<BookCard> findAllBookCardsWithStatus(CardStatus status) {
        return bookCardRepository.findAllByCardStatus(status);
    }

    @Override
    public BookCard findBookCardById(Long bookCardId) {
        return bookCardRepository.findBookCardById(bookCardId);
    }

    @Override
    public void deleteBookCardByBookCardId(Long bookCardId) {
        bookCardRepository.deleteById(bookCardId);
    }

    @Override
    public void deleteBookCardByUserId(Long userId) {
        bookCardRepository.deleteBookCardsByUserId(userId);
    }

    @Override
    public void deleteAllBookCardsByBookId(Long bookId) {
        bookCardRepository.deleteBookCardsByBookId(bookId);
    }
}
