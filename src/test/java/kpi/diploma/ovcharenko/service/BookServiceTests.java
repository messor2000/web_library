package kpi.diploma.ovcharenko.service;

import kpi.diploma.ovcharenko.config.PasswordEncoder;
import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookStatus;
import kpi.diploma.ovcharenko.entity.book.BookTag;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@AutoConfigureMockMvc
class BookServiceTests {

    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookRepository bookRepository;

    private final String category = "forTest";

    final Book book = new Book("test1", 9999, "testAuthor1", 1, "test1");

    @BeforeEach
    public void initEach() {
        bookService.addNewBook(book, category, "not used");
    }

    @AfterEach
    public void deleteEach() {
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("should correctly return book by book id")
    void returnBookByIdTest() {
        Book foundBook = bookService.findBookById(book.getId());

        assertEquals(foundBook, book);
    }

    @Test
    @DisplayName("should successfully delete book by book id")
    void deleteBookByIdTest() {
        Book forDelete = new Book("test1", 9999, "testAuthor1", 1, "test1");

        bookService.addNewBook(forDelete, "not used", "not used");

        Page<Book> booksBeforeDeleting = bookService.getAllBooks(Pageable.unpaged());

        bookService.deleteBookById(forDelete.getId());

        Page<Book> booksAfterDeleting = bookService.getAllBooks(Pageable.unpaged());

        assertEquals(booksBeforeDeleting.getTotalElements(), booksAfterDeleting.getTotalElements() + 1);
    }

    @Test
    @DisplayName("should successfully add new book")
    void addNewBookByIdTest() {
        Book newBook = new Book("test1", 9999, "testAuthor1", 1, "test1");

        Page<Book> booksBeforeAdding = bookService.getAllBooks(Pageable.unpaged());
        bookService.addNewBook(newBook, "not used", "not used");
        Page<Book> booksAfterAdding = bookService.getAllBooks(Pageable.unpaged());

        assertEquals(booksBeforeAdding.getTotalElements(), booksAfterAdding.getTotalElements() - 1);
    }

    @Test
    @DisplayName("should correctly update book")
    void updateBookTest() {
        final String name = "afterTesting";

        book.setBookName(name);

        bookService.updateBook(book, "not used", "not used");
        Book foundBook = bookService.findBookById(book.getId());

        assertNotSame(foundBook, book);
        assertEquals(foundBook.getBookName(), name);
    }

    @Test
    @DisplayName("should return all book in page way")
    void getAllBooksTest() {
        Page<Book> books = bookService.getAllBooks(Pageable.unpaged());

        assertEquals(1, books.getTotalElements());
    }

    @Test
    @DisplayName("should return all book in page way which contains special category")
    void getAllBooksByCategoryTest() {
        Book newBook = new Book("test1", 9999, "testAuthor1", 1, "test1");
        String category = "categoryForTest";
        bookService.addNewBook(newBook, category, "not used");

        Page<Book> books = bookService.getBookByCategory(Pageable.unpaged(), category);

        assertEquals(1, books.getTotalElements());
    }

    @Test
    @DisplayName("should return all categories")
    void findAllCategoriesTest() {
        Book newBook = new Book("test1", 9999, "testAuthor1", 1, "test1");
        String category = "categoryForTest";
        bookService.addNewBook(newBook, category, "not used");

        Set<String> allCategories = bookService.findAllCategories();

        assertEquals(2, allCategories.size());
    }

    @Test
    @DisplayName("should return all book categories")
    void findAllBookCategoriesTest() {
        Book newBook = new Book("test1", 9999, "testAuthor1", 1, "test1");
        String category = "categoryForTest";
        String secondCategory = "categoryForTest2";
        bookService.addNewBook(newBook, category, "not used");
        bookService.updateBook(newBook, secondCategory, "not used");

        Set<String> allBookCategories = bookService.findBookCategories(newBook);

        assertEquals(2, allBookCategories.size());
    }

    @Test
    @DisplayName("should successfully delete category from book")
    void deleteCategoryTest() {
        bookService.deleteCategory(book.getId(), category);

        Set<String> allBookCategories = bookService.findBookCategories(book);

        assertEquals(1, allBookCategories.size());
    }

    @Test
    @DisplayName("should return correctly book by book name")
    void findBookByBookName() {
        Page<Book> books = bookService.findByKeyWord("test1", Pageable.unpaged());

        Book foundBook = books.toList().get(0);

        assertEquals(foundBook, book);
    }

    @Test
    @DisplayName("should return correctly book by book author")
    void findBookByBookAuthor() {
        Page<Book> books = bookService.findByKeyWord("testAuthor1", Pageable.unpaged());

        Book foundBook = books.toList().get(0);

        assertEquals(foundBook, book);
    }

    @Test
    @DisplayName("should return correctly book by book year")
    void findBookByBookYear() {
        Page<Book> books = bookService.findByKeyWord("9999", Pageable.unpaged());

        Book foundBook = books.toList().get(0);

        assertEquals(foundBook, book);
    }

    @Test
    @DisplayName("should return empty collection if book doesnt present by entered key word")
    void returnEmptyListTest() {
        Page<Book> books = bookService.findByKeyWord("qwertyu", Pageable.unpaged());

        List<Book> listOfBooks = books.toList();

        assertEquals(Collections.emptyList(), listOfBooks);
    }

    @Test
    @DisplayName("should return two BookStatuses with status 'FREE'")
    void getAllBookStatusTest() {
        Book newBook = new Book("test1", 9999, "testAuthor1", 2, "test1");
        bookService.addNewBook(newBook, category, "not used");

        List<BookStatus> bookStatuses = bookService.getAllBooksStatus(newBook.getId());

        List<String> statuses = bookStatuses.stream()
                .map(BookStatus::getStatus)
                .collect(toList());

        assertThat(statuses).contains("FREE");
    }

    @Test
    @DisplayName("should return one BookStatus with status 'FREE' and one with status 'BOOKED'")
    void getAllBookStatusWithDifferentStatusesTest() {
        UserModel user = new UserModel().toBuilder()
                .firstName("test1")
                .lastName("test1")
                .email("test1@gmail.com")
                .password(PasswordEncoder.passwordEncoder().encode("password"))
                .build();
        userService.save(user);

        Book newBook = new Book("test1", 9999, "testAuthor1", 2, "test1");
        bookService.addNewBook(newBook, category, "not used");
        userService.bookedBook(newBook.getId(), user.getEmail());

        List<BookStatus> bookStatuses = bookService.getAllBooksStatus(newBook.getId());

        userService.deleteUser(userService.findByEmail(user.getEmail()).getId());

        List<String> statuses = bookStatuses.stream()
                .map(BookStatus::getStatus)
                .collect(toList());

        assertThat(statuses).contains("FREE", "BOOKED");
    }

    @Test
    @Transactional
    @DisplayName("should successfully delete tag from book")
    void deleteTagFromTheBookTest() {
        String tagName = "forDelete";
        BookTag bookTag = new BookTag(tagName);
        bookService.updateBook(book, null, bookTag.getTagName());

        bookService.deleteTagFromTheBook(book.getId(), tagName);

        assertFalse(bookService.findBookById(book.getId()).getTags().contains(bookTag));
    }
}
