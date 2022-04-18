package kpi.diploma.ovcharenko.service;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class BookServiceTests {

    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;

    private Book book;

    @BeforeEach
    void initTestUser() {
        book = new Book().toBuilder()
                .bookName("forTest")
                .author("forTestAuthor")
                .year(10000)
                .amount(1)
                .build();
        BookCategory bookCategory = new BookCategory("forTest");

        bookService.addNewBook(book, bookCategory.getCategory());
    }

    @AfterEach
    void deleteTestUser() {
        bookService.deleteBookById(book.getId());
    }

    @Test
    @DisplayName("Test get all pageable books")
    void getAllPageableBooksTest() {
        Page<Book> bookList = bookService.getAllBooks(PageRequest.of(1, 20));
        assertThat(bookList.getTotalElements()).isPositive();
    }

    @Test
    @DisplayName("Test get all pageable books by category")
    void getAllPageableBooksWithOneCategoryTest() {
        Page<Book> bookList = bookService.getBookByCategory(PageRequest.of(1, 20), "Без категории");
        assertThat(bookList.getTotalElements()).isPositive();
    }

    @Test
    @DisplayName("Test add new book")
    void addNewBookTest() {
        String bookNameForTest = "forTest1";
        Book book = new Book().toBuilder()
                .bookName("forTest1")
                .amount(1)
                .build();
        BookCategory bookCategory = new BookCategory("testCategory");

        bookService.addNewBook(book, bookCategory.getCategory());

        Book foundBook = bookService.findBookById(book.getId());

        bookService.deleteBookById(book.getId());

        assertEquals(bookNameForTest, foundBook.getBookName());
    }

    @Test
    @DisplayName("Test find book by name")
    void findBookByName() {
        Page<Book> pagedBook = bookService.findByKeyWord("forTest", PageRequest.of(0, 20));
        Book foundBook = pagedBook.toList().get(0);

        assertEquals(book.getBookName(), foundBook.getBookName());
    }

    @Test
    @DisplayName("Test find book by author")
    void findBookByAuthorTest() {
        Page<Book> pagedBook = bookService.findByKeyWord("forTestAuthor", PageRequest.of(0, 20));
        Book foundBook = pagedBook.toList().get(0);

        assertEquals(book.getBookName(), foundBook.getBookName());
    }

    @Test
    @DisplayName("Test find book by year")
    void findBookByYearTest() {
        Page<Book> pagedBook = bookService.findByKeyWord(String.valueOf(10000), PageRequest.of(0, 20));
        Book foundBook = pagedBook.toList().get(0);

        assertEquals(book.getBookName(), foundBook.getBookName());
    }

    @Test
    @DisplayName("Test find all books categories")
    void findAllCategoriesTest() {
        Set<String> categoriesBeforeAddingNewOne = bookService.findAllCategories();
        bookService.updateBook(book, "newBookCategory");
        Set<String> categoriesAfterAddingNewOne = bookService.findAllCategories();

        assertEquals(categoriesBeforeAddingNewOne.size() + 1, categoriesAfterAddingNewOne.size());
    }

    @Test
    @DisplayName("Test find all book categories")
    void findBookCategoriesTest() {
        bookService.updateBook(book, "testCategory2");

        Set<String> bookAllCategories = bookService.findBookCategories(book);

        assertEquals(2, bookAllCategories.size());
    }

    @Test
    @DisplayName("Test delete book category and found one is previous book category")
    void deleteBookAndCheckTheRemainingCategory() {
        String testBookCategory = "testCategory2";
        bookService.updateBook(book, testBookCategory);

        bookService.deleteCategory(book.getId(), testBookCategory);
        Set<String> bookAllCategories = bookService.findBookCategories(book);

        assertTrue(bookAllCategories.contains(testBookCategory));
    }

    @Test
    @DisplayName("Test get one book that taken by user")
    void getBookThatTakenByUserTest() {
        UserModel userModel = new UserModel().toBuilder()
                .firstName("forTest")
                .lastName("forTest")
                .email("forTest@gmail.com")
                .password("forTest")
                .build();

        userService.save(userModel);

        userService.takeBook(book.getId(), userModel.getEmail());

        List<Book> allTakenBooks = bookService.getAllBooksThatTaken(userModel.getEmail());

        userService.returnBook(book.getId(), userModel.getEmail());
        userService.deleteUserByEmail(userModel.getEmail());

        assertEquals(1, allTakenBooks.size());
    }

    @Test
    @DisplayName("Test get null book that taken and returned by user")
    void getBookThatTakenAndReturnedByUserTest() {
        UserModel userModel = new UserModel().toBuilder()
                .firstName("forTest")
                .lastName("forTest")
                .email("forTest@gmail.com")
                .password("forTest")
                .build();

        userService.save(userModel);

        userService.takeBook(book.getId(), userModel.getEmail());
        userService.returnBook(book.getId(), userModel.getEmail());

        List<Book> allTakenBooks = bookService.getAllBooksThatTaken(userModel.getEmail());

        userService.deleteUserByEmail(userModel.getEmail());

        assertEquals(0, allTakenBooks.size());
    }
}
