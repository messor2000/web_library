package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.card.BookCard;

import java.util.List;

public interface BookCardService {
    List<BookCard> findAllBookCards();

    List<BookCard> findAllUserBookCards(Long userId);
}
