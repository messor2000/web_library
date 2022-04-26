package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.card.CardStatus;
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
    public List<BookCard> findAllBookCards() {
        return bookCardRepository.findAll();
    }

    @Override
    public List<BookCard> findAllUserBookCards(Long userId) {
        return bookCardRepository.findAllByUserId(userId);
    }

    @Override
    public List<BookCard> findAllUserBookCardsAndStatus(Long userId, CardStatus status) {
        return bookCardRepository.findAllByUserIdAndCardStatus(userId, status);
    }

    @Override
    public List<BookCard> findAllBookCardsWithStatus(CardStatus status) {
        return bookCardRepository.findAllByCardStatus(status);
    }

    @Override
    @Transactional
    public void deleteBookCard(Long bookCardId) {
        bookCardRepository.deleteBookCardById(bookCardId);
    }
}
