package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.config.PasswordEncoder;
import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureMockMvc
public class BookCardRepositoryTests {
    @Autowired
    private BookCardRepository bookCardRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;

    Book book = new Book("test1", 9999, "testAuthor1", 1, "test1");
    UserModel user = new UserModel().toBuilder()
            .firstName("test1")
            .lastName("test1")
            .email("test1@gmail.com")
            .password(PasswordEncoder.passwordEncoder().encode("password"))
            .build();

    @BeforeEach
    public void initEach() {
        bookService.addNewBook(book, "not used", "not used");
        userService.save(user);

        userService.bookedBook(book.getId(), user.getEmail());
    }

    @AfterEach
    public void deleteEach() {
        bookService.deleteBookById(book.getId());
        userService.deleteUser(userService.findByEmail(user.getEmail()).getId());
    }

    @Test
    @DisplayName("should return list of book cards")
    void returnListOfBooks() {
        List<BookCard> bookCards = bookCardRepository.findAll();

        assertEquals(1, bookCards.size());
    }

    @Test
    @DisplayName("should return list of book cards by user id")
    void returnListOfBooksByUserId() {
        AppUser foundUser = userService.findByEmail(user.getEmail());
        List<BookCard> bookCards = bookCardRepository.findAllByUserId(foundUser.getId());

        assertEquals(1, bookCards.size());
    }

    @Test
    @Transactional
    @DisplayName("should return list of book cards which card status booked")
    void returnListOfBooksByStatusBooked() {
        List<BookCard> bookCards = bookCardRepository.findAllByCardStatus("BOOKED");

        assertEquals(bookCards.get(0).getCardStatus(), "BOOKED");
    }

    @Test
    @DisplayName("should return list of book cards which card status approved")
    void returnListOfBooksByStatusApproved() {
        AppUser foundUser = userService.findByEmail(user.getEmail());
        List<BookCard> bookCards = bookCardRepository.findAllByUserId(foundUser.getId());
        userService.approveBookForUser(bookCards.get(0).getId());

        List<BookCard> bookCardsAfterApproving = bookCardRepository.findAllByCardStatus("APPROVED");

        assertEquals(bookCardsAfterApproving.get(0).getCardStatus(), "APPROVED");
    }

    @Test
    @DisplayName("should correctly return book card by card id")
    void returnBookCardByCardId() {
        List<BookCard> bookCards = bookCardRepository.findAll();

        BookCard bookCard = bookCardRepository.findBookCardById(bookCards.get(0).getId());

        assertEquals(bookCards.get(0), bookCard);
    }

    @Test
    @Transactional
    @DisplayName("should correctly delete book card by card id")
    void deleteBookCardByCardId() {
        List<BookCard> bookCards = bookCardRepository.findAll();
        BookCard bookCard = bookCardRepository.findBookCardById(bookCards.get(0).getId());

        bookCardRepository.deleteBookCardByCardId(bookCard.getId());

        assertNull(bookCardRepository.findBookCardById(bookCards.get(0).getId()));
    }

    @Test
    @Transactional
    @DisplayName("should correctly delete book card by user id")
    void deleteBookCardByUserId() {
        AppUser foundUser = userService.findByEmail(user.getEmail());
        List<BookCard> bookCards = bookCardRepository.findAllByUserId(foundUser.getId());

        bookCardRepository.deleteBookCardByCardId(foundUser.getId());

        assertNull(bookCardRepository.findBookCardById(bookCards.get(0).getId()));
    }

    @Test
    @Transactional
    @DisplayName("should correctly delete book card by book id")
    void deleteBookCardByBookId() {
        AppUser foundUser = userService.findByEmail(user.getEmail());
        List<BookCard> bookCards = bookCardRepository.findAllByUserId(foundUser.getId());

        bookCardRepository.deleteBookCardByCardId(book.getId());

        assertNull(bookCardRepository.findBookCardById(bookCards.get(0).getId()));
    }
}
