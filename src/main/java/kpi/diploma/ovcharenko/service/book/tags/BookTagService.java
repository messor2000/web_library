package kpi.diploma.ovcharenko.service.book.tags;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookTag;

import java.util.Set;

public interface BookTagService {
    Set<String> getAllBookTags();

    Set<BookTag> getAllTags();

    Set<String> findBookTags(Book book);

    Set<BookTag> findBooksByTag(Set<String> bookTags);

    void deleteBookTagByBook(Book book);

    boolean existBookTagByBookAndTag(String tag, Book book);
}
