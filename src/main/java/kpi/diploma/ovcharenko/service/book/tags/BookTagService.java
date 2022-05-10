package kpi.diploma.ovcharenko.service.book.tags;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookTag;

import java.util.Optional;
import java.util.Set;

public interface BookTagService {
    void saveBookTag(BookTag bookTag);

    Set<String> findAllTagNames();

    Set<BookTag> findBookTagByBook(Set<Book> books);

    Optional<BookTag> findBookTagByTagName(String tagName);

    Set<String> findBookTagsByBookId(Long bookId);

    Set<String> findBookNameByTag(String tag);
}
