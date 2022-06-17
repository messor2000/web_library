package kpi.diploma.ovcharenko.service;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookTag;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.repo.BookTagRepository;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.book.tags.BookTagService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookTagServiceTests {
    @Autowired
    private BookTagService bookTagService;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookTagRepository bookTagRepository;

    final Book book = new Book("test1", 9999, "testAuthor1", 1, "test1");
    final BookTag bookTag = new BookTag("new book tag");

    @BeforeEach
    public void initEach() {
        bookService.addNewBook(book, "forTest", null);
        bookTagService.saveBookTag(bookTag);
    }

    @AfterEach
    public void deleteEach() {
        bookRepository.deleteAll();
        bookTagRepository.deleteAll();
    }

    @Test
    @DisplayName("should save new book tag to the book and succesfylly return set of book tags with new one")
    void saveBookTagThenReturnWithAllWithNewOneTest() {
        Set<BookTag> allTags = bookTagService.findBookTagByBook(Collections.singleton(book));
        allTags.add(bookTag);
        book.setTags(allTags);
        bookService.updateBook(book, null, null);

        Set<BookTag> bookTags = bookTagService.findBookTagByBook(Collections.singleton(book));

        assertTrue(bookTags.contains(bookTag));
    }

    @Test
    @DisplayName("should return all book tags")
    void findAllTagsNamesTest() {
        Set<String> allBookTagNames = bookTagService.findAllTagNames();

        assertTrue(allBookTagNames.contains(bookTag.getTagName()));
    }

    @Test
    @DisplayName("should return correctly book tag by tag name")
    void findBookTagByTagNameTest() {
        BookTag foundBookTag = bookTagService.findBookTagByTagName(bookTag.getTagName()).get();

        assertEquals(foundBookTag, bookTag);
    }

    @Test
    @DisplayName("should return correctly book tag by book id")
    void findBookTagsByBookIdTest() {
        Set<BookTag> allTags = bookTagService.findBookTagByBook(Collections.singleton(book));
        allTags.add(bookTag);
        book.setTags(allTags);
        bookService.updateBook(book, null, null);

        Set<String> foundBookTags = bookTagService.findBookTagsByBookId(book.getId());

        assertTrue(foundBookTags.contains(bookTag.getTagName()));
    }

    @Test
    @Transactional
    @DisplayName("should successfully delete book tag")
    void deleteBookTagTEst() {
        BookTag newBookTag = new BookTag("new book tag 2");
        bookTagService.saveBookTag(newBookTag);

        bookTagService.deleteBookTag(newBookTag);

        Set<String> foundBookTags = bookTagService.findAllTagNames();

        assertFalse(foundBookTags.contains(newBookTag.getTagName()));
    }
}
