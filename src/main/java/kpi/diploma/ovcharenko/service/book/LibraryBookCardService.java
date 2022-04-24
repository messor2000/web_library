package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.repo.BookCardRepository;
import org.springframework.stereotype.Service;

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
}
