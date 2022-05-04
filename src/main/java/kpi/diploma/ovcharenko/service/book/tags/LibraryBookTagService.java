package kpi.diploma.ovcharenko.service.book.tags;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookTag;
import kpi.diploma.ovcharenko.repo.BookTagRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class LibraryBookTagService implements BookTagService {

    private final BookTagRepository bookTagRepository;

    public LibraryBookTagService(BookTagRepository bookTagRepository) {
        this.bookTagRepository = bookTagRepository;
    }

    @Override
    public Set<BookTag> getAllTags() {
        return bookTagRepository.findAll();
    }

    @Override
    public Set<String> getAllBookTags() {
        Set<String> stringBookTags = new HashSet<>();
        Set<BookTag> bookTags = bookTagRepository.findAll();
        for (BookTag bookTag: bookTags) {
            stringBookTags.add(bookTag.getTagName());
        }

        return stringBookTags;
    }

    @Override
    public Set<String> findBookTags(Book book) {
        Set<BookTag> tags = book.getTags();

        Set<String> bookTags = new HashSet<>();

        for (BookTag bookTag : tags) {
            bookTags.add(bookTag.getTagName());
        }

        return bookTags;
    }

    @Override
    public Set<BookTag> findBooksByTag(Set<String> bookTag) {
        if(bookTag.isEmpty()) {
            return bookTagRepository.findAll();
        }
        return bookTagRepository.findByTags(bookTag);
    }

    @Override
    public void deleteBookTagByBook(Book book){
        bookTagRepository.deleteBookTagByBook(book);
    }

    @Override
    public boolean existBookTagByBookAndTag(String tag, Book book) {
        return bookTagRepository.existsBookTagByTagNameAndBook(tag, book);
    }
}
