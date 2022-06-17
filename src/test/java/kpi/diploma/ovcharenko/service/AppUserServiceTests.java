package kpi.diploma.ovcharenko.service;

import kpi.diploma.ovcharenko.config.PasswordEncoder;
import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookStatus;
import kpi.diploma.ovcharenko.entity.card.BookCard;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.entity.user.VerificationToken;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.book.cards.BookCardService;
import kpi.diploma.ovcharenko.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AppUserServiceTests {

    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookCardService bookCardService;

    private final String token = UUID.randomUUID().toString();

    final String password = "password";
    UserModel user = new UserModel().toBuilder()
            .firstName("test1")
            .lastName("test1")
            .email("test1@gmail.com")
            .password(PasswordEncoder.passwordEncoder().encode(password))
            .build();

    Book book = new Book("test1", 9999, "testAuthor1", 1, "test1");

    @BeforeEach
    public void initEach() {
        userService.save(user);
        bookService.addNewBook(book, "not used", "not used");
    }

    @AfterEach
    public void deleteEach() {
        userService.deleteUser(userService.findByEmail(user.getEmail()).getId());
        bookService.deleteBookById(book.getId());
    }

    @Test
    @DisplayName("should return correct user by user email")
    void findUserByEmailTest() {
        AppUser foundUser = userService.findByEmail(user.getEmail());

        assertEquals(foundUser.getEmail(), user.getEmail());
        assertEquals(foundUser.getFirstName(), user.getFirstName());
    }

    @Test
    @DisplayName("should return correct user by user id")
    void findUserByIdTest() {
        AppUser foundUser = userService.findByEmail(user.getEmail());
        AppUser foundUserById = userService.findById(foundUser.getId());

        assertEquals(foundUserById, foundUser);
    }

    @Test
    @DisplayName("should successfully save new user")
    void saveNewUserTest() {
        UserModel userForSave = new UserModel().toBuilder()
                .firstName("test1")
                .lastName("test1")
                .email("test2@gmail.com")
                .password(PasswordEncoder.passwordEncoder().encode("password"))
                .build();

        List<AppUser> appUsersBeforeSaving = userService.showAllUsers();

        userService.save(userForSave);

        List<AppUser> appUsersAfterSaving = userService.showAllUsers();

        AppUser foundUser = userService.findByEmail(user.getEmail());
        AppUser foundUserForThisTest = userService.findByEmail(userForSave.getEmail());

        userService.deleteUser(foundUserForThisTest.getId());

        assertNotSame(foundUser, foundUserForThisTest);
        assertNotSame(appUsersBeforeSaving, appUsersAfterSaving);
    }

    @Test
    @DisplayName("should successfully save new user when admin saves him")
    void saveNewUserByAdminTest() {
        UserModel userForSave = new UserModel().toBuilder()
                .firstName("test1")
                .lastName("test1")
                .email("test2@gmail.com")
                .password(PasswordEncoder.passwordEncoder().encode("password"))
                .build();

        List<AppUser> appUsersBeforeSaving = userService.showAllUsers();

        userService.createNewUserByAdmin(userForSave);

        List<AppUser> appUsersAfterSaving = userService.showAllUsers();

        AppUser foundUser = userService.findByEmail(user.getEmail());
        AppUser foundUserForThisTest = userService.findByEmail(userForSave.getEmail());

        userService.deleteUser(foundUserForThisTest.getId());

        assertNotSame(foundUser, foundUserForThisTest);
        assertNotSame(appUsersBeforeSaving, appUsersAfterSaving);
    }

    @Test
    @DisplayName("user must become available after save him like registered user")
    void saveRegisteredUserTest() {
        AppUser foundUser = userService.findByEmail(user.getEmail());

        userService.saveRegisteredUser(foundUser);

        AppUser foundUserAfterUpdating = userService.findByEmail(user.getEmail());

        assertTrue(foundUserAfterUpdating.isEnabled());
    }

    @Test
    @DisplayName("should successfully delete user by user id")
    void deleteUserTest() {
        UserModel userForDelete = new UserModel().toBuilder()
                .firstName("test1")
                .lastName("test1")
                .email("test3@gmail.com")
                .password(PasswordEncoder.passwordEncoder().encode("password"))
                .build();

        userService.save(userForDelete);

        AppUser foundUser = userService.findByEmail(userForDelete.getEmail());
        userService.deleteUser(foundUser.getId());

        assertNull(userService.findByEmail(userForDelete.getEmail()));
    }

    @Test
    @DisplayName("when user book a book, should change status in book to 'booked' and book card status into 'wait for approve'")
    void bookABookByUserTest() {
        userService.bookedBook(book.getId(), user.getEmail());

        Book bookAfterBooking = bookService.findBookById(book.getId());

        Set<BookStatus> bookStatusSet = bookAfterBooking.getStatuses();
        BookStatus bookStatus = bookStatusSet.stream().findAny().get();

        List<BookCard> bookCard = bookCardService.findAllBookCards();
        BookCard foundBookCard = bookCard.stream().findAny().get();

        assertEquals("BOOKED", bookStatus.getStatus());
        assertEquals("WAIT_FOR_APPROVE", foundBookCard.getCardStatus());
    }

    @Test
    @DisplayName("when admin approve a book, should change status in book to 'taken' and book card status into 'approved'")
    void approveBookByAdminTest() {
        userService.bookedBook(book.getId(), user.getEmail());
        List<BookCard> allBookCards = bookCardService.findAllBookCards();
        BookCard bookCard = allBookCards.stream().findAny().get();

        userService.approveBookForUser(bookCard.getId());

        List<BookCard> allBookCardsAfterApprove = bookCardService.findAllBookCards();
        BookCard foundBookCard = allBookCardsAfterApprove.stream().findAny().get();

        Set<BookStatus> bookStatusSet = foundBookCard.getBook().getStatuses();
        BookStatus bookStatus = bookStatusSet.stream().findAny().get();

        assertEquals("TAKEN", bookStatus.getStatus());
        assertEquals("APPROVED", foundBookCard.getCardStatus());
    }

    @Test
    @DisplayName("when admin reject a book, should change status in book to 'free' and book card status into 'rejected'")
    void rejectBookByAdminTest() {
        userService.bookedBook(book.getId(), user.getEmail());
        List<BookCard> allBookCards = bookCardService.findAllBookCards();
        BookCard bookCard = allBookCards.stream().findAny().get();

        userService.rejectTheBook(bookCard.getId());

        List<BookCard> allBookCardsAfterApprove = bookCardService.findAllBookCards();
        BookCard foundBookCard = allBookCardsAfterApprove.stream().findAny().get();

        Set<BookStatus> bookStatusSet = foundBookCard.getBook().getStatuses();
        BookStatus bookStatus = bookStatusSet.stream().findAny().get();

        assertEquals("FREE", bookStatus.getStatus());
        assertEquals("REJECT", foundBookCard.getCardStatus());
    }

    @Test
    @DisplayName("when user return a book and admin put book into the library, " +
            "should change status in book to 'free' and book card status into 'book returned'")
    void returnBookByAdminTest() {
        userService.bookedBook(book.getId(), user.getEmail());
        List<BookCard> allBookCards = bookCardService.findAllBookCards();
        BookCard bookCard = allBookCards.stream().findAny().get();

        userService.approveBookForUser(bookCard.getId());
        userService.returnTheBook(bookCard.getId());

        List<BookCard> allBookCardsAfterApprove = bookCardService.findAllBookCards();
        BookCard foundBookCard = allBookCardsAfterApprove.stream().findAny().get();

        Set<BookStatus> bookStatusSet = foundBookCard.getBook().getStatuses();
        BookStatus bookStatus = bookStatusSet.stream().findAny().get();

        assertEquals("FREE", bookStatus.getStatus());
        assertEquals("BOOK_RETURNED", foundBookCard.getCardStatus());
    }

    @Test
    @DisplayName("should successfully delete book card")
    void deleteBookCardById() {
        userService.bookedBook(book.getId(), user.getEmail());
        List<BookCard> allBookCards = bookCardService.findAllBookCards();
        BookCard bookCard = allBookCards.stream().findAny().get();

        userService.deleteBookCard(bookCard.getId());

        assertEquals(bookCardService.findAllBookCards(), Collections.emptyList());
    }

    @Test
    @DisplayName("should successfully create verification token for user and got this user by created token")
    void createVerificationTokenForUser() {
        AppUser foundUser = userService.findByEmail(user.getEmail());

        userService.createVerificationTokenForUser(foundUser, token);

        AppUser userByVerificationToken = userService.getUserByVerificationToken(token);

        assertEquals(userByVerificationToken, foundUser);
    }

    @Test
    @DisplayName("should successfully recreate verification token for user and got this user by created token")
    void createNewVerificationTokenForUser() {
        AppUser foundUser = userService.findByEmail(user.getEmail());

        userService.createVerificationTokenForUser(foundUser, token);

        AppUser userByVerificationToken = userService.getUserByVerificationToken(token);

        VerificationToken verificationToken = userService.generateNewVerificationToken(token);

        AppUser userByUpdatedVerificationToken = userService.getUserByVerificationToken(verificationToken.getToken());

        assertEquals(userByUpdatedVerificationToken, userByVerificationToken);
    }

    @Test
    @DisplayName("should successfully return the same verification token by token")
    void returnVerificationTokenByToken() {
        AppUser foundUser = userService.findByEmail(user.getEmail());

        userService.createVerificationTokenForUser(foundUser, token);

        VerificationToken verificationToken = userService.getVerificationToken(token);

        assertEquals(verificationToken.getToken(), token);
    }

    @Test
    @DisplayName("should successfully create verification token for user and got this user by created token")
    void createPasswordResetTokenForUser() {
        AppUser foundUser = userService.findByEmail(user.getEmail());

        userService.createPasswordResetTokenForUser(foundUser, token);

        AppUser userByPasswordVerificationToken = userService.getUserByPasswordResetToken(token).get();

        assertEquals(userByPasswordVerificationToken, foundUser);
    }

    @Test
    @DisplayName("should correctly change user password")
    void changeUserPasswordTest() {
        AppUser foundUser = userService.findByEmail(user.getEmail());

        System.out.println(foundUser.getPassword());

        userService.changeUserPassword(foundUser, "new password");

        AppUser userAfterChangingPassword = userService.findByEmail(user.getEmail());

        System.out.println(userAfterChangingPassword.getPassword());

        assertNotEquals(foundUser.getPassword(), userAfterChangingPassword.getPassword());
    }

    @Test
    @DisplayName("should return true if user enter the same password")
    void checkIfValidOldPassword() {
        AppUser foundUser = userService.findByEmail(user.getEmail());

        assertTrue(userService.checkIfValidOldPassword(foundUser, password));
    }

    @Test
    @DisplayName("should return false if user enter the different password")
    void checkIfInvalidOldPassword() {
        AppUser foundUser = userService.findByEmail(user.getEmail());

        assertFalse(userService.checkIfValidOldPassword(foundUser, "new password"));
    }

    @Test
    @DisplayName("should return list of all users")
    void returnListOfUsers() {
        String email = "test2@gmail.com";
        UserModel newUser = new UserModel().toBuilder()
                .firstName("test1")
                .lastName("test1")
                .email(email)
                .password(PasswordEncoder.passwordEncoder().encode("password"))
                .build();

        userService.save(newUser);

        List<AppUser> appUsers = userService.showAllUsers();
        userService.deleteUser(userService.findByEmail(email).getId());

        assertEquals(2, appUsers.size());
    }

    @Test
    @DisplayName("should successfully update user's username")
    void updateUserTest() {
        AppUser foundUser = userService.findByEmail(user.getEmail());

        final String updatedName = "updated name";
        UserModel updatedUser = new UserModel().toBuilder()
                .firstName(updatedName)
                .lastName("test1")
                .email("test1@gmail.com")
                .password(PasswordEncoder.passwordEncoder().encode("password"))
                .build();

        userService.updateUser(foundUser.getId(), updatedUser, true);

        AppUser userAfterUpdate = userService.findByEmail(updatedUser.getEmail());

        assertNotSame(foundUser.getFirstName(), updatedUser.getFirstName());
        assertEquals(userAfterUpdate.getFirstName(), updatedName);
    }
}


























