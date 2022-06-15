package kpi.diploma.ovcharenko.service;

import kpi.diploma.ovcharenko.config.PasswordEncoder;
import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookStatus;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
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

import java.util.List;

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

    UserModel user = new UserModel().toBuilder()
            .firstName("test1")
            .lastName("test1")
            .email("test1@gmail.com")
            .password(PasswordEncoder.passwordEncoder().encode("password"))
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
                .email("test2@gmail.com")
                .password(PasswordEncoder.passwordEncoder().encode("password"))
                .build();

        userService.save(userForDelete);

        AppUser foundUser = userService.findByEmail(userForDelete.getEmail());
        userService.deleteUser(foundUser.getId());

        assertNull(userService.findByEmail(userForDelete.getEmail()));
    }

    @Test
    @DisplayName("when user book a book, should change status in book to 'booked'")
    void bookABookByUserTest() {
        userService.bookedBook(book.getId(), user.getEmail());

        Book bookAfterBooking = bookService.findBookById(book.getId());

        assertTrue(bookAfterBooking.getStatuses().contains(new BookStatus(Status.BOOKED)));
    }

//    @Test
//    @DisplayName("when admin approve a book, should change status in book to 'taken'")
//    void approveBookByAdminTest() {
//        userService.approveBookForUser(book.getId(), user.getEmail());
//
//        Book bookAfterBooking = bookService.findBookById(book.getId());
//
//        assertTrue(bookAfterBooking.getStatuses().contains(new BookStatus(Status.BOOKED)));
//    }
}


























