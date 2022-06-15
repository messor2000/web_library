package kpi.diploma.ovcharenko.service.book.cards;

import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.repo.BookCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<BookCard> findAllUserBookCards(Long userId) {
        return bookCardRepository.findAllByUserId(userId);
    }

    @Override
    public List<BookCard> findAllBookCardsWithStatus(String status) {
        return bookCardRepository.findAllByCardStatus(status);
    }

    @Override
    public BookCard findBookCardById(Long bookCardId) {
        return bookCardRepository.findBookCardById(bookCardId);
    }

    @Override
    @Transactional
    public void deleteBookCardByBookCardId(Long bookCardId) {
        bookCardRepository.deleteBookCardByCardId(bookCardId);
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
