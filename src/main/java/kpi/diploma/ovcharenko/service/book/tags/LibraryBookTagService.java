package kpi.diploma.ovcharenko.service.book.tags;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookTag;
import kpi.diploma.ovcharenko.repo.BookTagRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class LibraryBookTagService implements BookTagService {

    private final BookTagRepository bookTagRepository;

    public LibraryBookTagService(BookTagRepository bookTagRepository) {
        this.bookTagRepository = bookTagRepository;
    }

    @Override
    public void saveBookTag(BookTag bookTag) {
        bookTagRepository.save(bookTag);
    }

    @Override
    public Set<String> findAllTagNames() {
        Set<String> bookTagsName = new HashSet<>();
        Set<BookTag> bookTags = bookTagRepository.findAll();
        for (BookTag bookTag: bookTags) {
            bookTagsName.add(bookTag.getTagName());
        }

        return bookTagsName;
    }

    @Override
    public Set<BookTag> findBookTagByBook(Set<Book> books) {
        return bookTagRepository.findAllByBooksIn(books);
    }

    @Override
    public Optional<BookTag> findBookTagByTagName(String tagName) {
        return bookTagRepository.findBookTagByTagName(tagName);
    }

    @Override
    public Set<String> findBookTagsByBookId(Long bookId) {
        Set<String> bookTagsName = new HashSet<>();
        Set<BookTag> bookTags = bookTagRepository.findByBooks_Id(bookId);
        for (BookTag bookTag: bookTags) {
            bookTagsName.add(bookTag.getTagName());
        }

        return bookTagsName;
    }

    @Override
    public void deleteBookTag(BookTag bookTag) {
        bookTagRepository.delete(bookTag);
    }
}
