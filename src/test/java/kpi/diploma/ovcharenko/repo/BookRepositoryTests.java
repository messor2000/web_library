package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.service.book.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookService bookService;

    Book book1 = new Book("test1", 9999, "testAuthor1", 1, "test1");
    Book book2 = new Book("test2", 1000, "testAuthor2", 1, "test2");

    @BeforeEach
    public void initEach() {
        List<Book> bookList = new ArrayList<>();

        bookList.add(book1);
        bookList.add(book2);

        bookRepository.saveAll(bookList);
    }

    @AfterEach
    public void deleteEach() {
        bookRepository.delete(book1);
        bookRepository.delete(book2);
    }

    @Test
    @DisplayName("should return list of books")
    void returnListOfBooks() {
        Page<Book> books = bookRepository.findAll(PageRequest.of(0, 20));

        assertEquals(2, books.stream().count());
    }

    @Test
    @Transactional
    @DisplayName("should return only book with selected category")
    void findBookCategoryContainsTest() {
        String category = "forTestCategory";

        Book book = new Book().toBuilder()
                .bookName("forTest")
                .amount(1)
                .build();

        bookService.addNewBook(book, category, "not used");

        Page<Book> books = bookRepository.findByCategoryContains(category, PageRequest.of(0, 20));

        assertEquals(1, books.get().count());
    }

    @Test
    @DisplayName("should return the book which contains name")
    void findBookByNameTest() {
        Page<Book> foundBook = bookRepository.findByBookNameOrAuthor(book1.getBookName(), PageRequest.of(0, 20));

        assertEquals(1, foundBook.get().count());
    }

    @Test
    @DisplayName("should return the book which contains author")
    void findBookByAuthorTest() {
        Page<Book> foundBook = bookRepository.findByBookNameOrAuthor(book1.getAuthor(), PageRequest.of(0, 20));

        assertEquals(1, foundBook.get().count());
    }

    @Test
    @DisplayName("should return the book which contains year")
    void findBookByYearTest() {
        Page<Book> foundBook = bookRepository.findByYearContaining(book1.getYear(), PageRequest.of(0, 20));

        assertEquals(1, foundBook.get().count());
    }

    @Test
    @DisplayName("should successfully delete book by id")
    void deleteBookByIdTest() {
        Page<Book> foundBook = bookRepository.findByBookNameOrAuthor(book1.getBookName(), PageRequest.of(0, 20));

        Book book = foundBook.get().findFirst().get();

        bookRepository.deleteBookById(book.getId());

        assertEquals(Optional.empty(), bookRepository.findById(book.getId()));
    }
}
