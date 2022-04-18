package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.service.book.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("Test save list of books")
    void saveListOfBooksTest() {
        Page<Book> books = bookRepository.findAll(PageRequest.of(1, 20));

        assertEquals(20, books.stream().count());
    }

    @Test
    @DisplayName("Test find book with category")
    void findBookCategoryContainsTest() {
        String category = "forTestCategory";
        Book book = new Book().toBuilder()
                .bookName("forTest")
                .amount(1)
                .build();

        bookService.addNewBook(book, category);

        Page<Book> books = bookRepository.findByCategoryContains(category ,PageRequest.of(0, 20));

        bookService.deleteBookById(book.getId());

        assertEquals(1, books.get().count());
    }

    @Test
    @DisplayName("Test find book by name")
    void findBookByNameTest() {
        String name = "forTest";
        Book book = new Book().toBuilder()
                .bookName(name)
                .amount(1)
                .build();

        bookService.addNewBook(book, "forTestCategory");

        Page<Book> foundBook = bookRepository.findByBookNameOrAuthorOrYear(name ,PageRequest.of(0, 20));

        bookService.deleteBookById(book.getId());

        assertEquals(1, foundBook.get().count());
    }

    @Test
    @DisplayName("Test find book by author")
    void findBookByAuthorTest() {
        String author = "forTest";
        Book book = new Book().toBuilder()
                .bookName("forTest")
                .author(author)
                .amount(1)
                .build();

        bookService.addNewBook(book, "forTestCategory");

        Page<Book> foundBook = bookRepository.findByBookNameOrAuthorOrYear(author ,PageRequest.of(0, 20));

        bookService.deleteBookById(book.getId());

        assertEquals(1, foundBook.get().count());
    }

    @Test
    @DisplayName("Test find book by book year")
    void findBookByYearTest() {
        int year = 10000;
        Book book = new Book().toBuilder()
                .bookName("forTest")
                .amount(1)
                .year(year)
                .build();

        bookService.addNewBook(book, "forTestCategory");

        Page<Book> foundBook = bookRepository.findByBookNameOrAuthorOrYear(String.valueOf(year),PageRequest.of(0, 20));

        bookService.deleteBookById(book.getId());

        assertEquals(1, foundBook.get().count());
    }
}
