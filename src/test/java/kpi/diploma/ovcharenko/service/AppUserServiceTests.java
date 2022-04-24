package kpi.diploma.ovcharenko.service;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import kpi.diploma.ovcharenko.entity.user.AppUser;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class AppUserServiceTests {

    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    UserModel userModel;
    private final String email = "forTest@gmail.com";
    private Book book;

    @BeforeEach
    void initTestUserAndTestBook() {
        userModel = new UserModel().toBuilder()
                .firstName("forTest")
                .lastName("forTest")
                .email(email)
                .password("forTest")
                .build();

        userService.save(userModel);

        book = new Book().toBuilder()
                .bookName("forTest")
                .amount(1)
                .build();
        BookCategory bookCategory = new BookCategory("forTest");

        bookService.addNewBook(book, bookCategory.getCategory());
    }

    @AfterEach
    void deleteTestUserAndTestBook() {
        AppUser user = userService.findByEmail(email);
        userService.deleteUser(user.getId());
        bookService.deleteBookById(book.getId());
    }

    @Test
    @DisplayName("Test find user by email")
    void saveAndRetrieveNewUserByEmail() {
        AppUser foundUser = userService.findByEmail(email);

        assertEquals(foundUser.getEmail(), email);
    }

    @Test
    @DisplayName("Test change old user password to the new one")
    void changeOldPasswordTest() {
        AppUser foundUser = userService.findByEmail(email);

        String newUserPassword = "newpassword";
        userService.changeUserPassword(foundUser, newUserPassword);
        AppUser userAfterChangePassword  = userService.findByEmail(email);

        assertTrue(passwordEncoder.matches(newUserPassword, userAfterChangePassword.getPassword()));
    }

    @Test
    @DisplayName("Test check that user enter valid old password")
    void checkValidUserOldPassword() {
        AppUser foundUser = userService.findByEmail(email);

        assertTrue(userService.checkIfValidOldPassword(foundUser, "forTest"));
    }

    @Test
    @DisplayName("Test show all users")
    void showAllUsersTest() {
        UserModel userModel = new UserModel().toBuilder()
                .firstName("forTest")
                .lastName("forTest")
                .email("forTest2@gmail.com")
                .password("forTest")
                .build();

        List<AppUser> appUserList = userService.showAllUsers();
        userService.save(userModel);
        List<AppUser> appUserListAfterSavingNewOne = userService.showAllUsers();

        AppUser user = userService.findByEmail(email);
        userService.deleteUser(user.getId());

        assertEquals(appUserList.size() + 1, appUserListAfterSavingNewOne.size());
    }

//    @Test
//    @DisplayName("Test take book by user")
//    void testTakeBook() {
//        AppUser foundUser = userService.findByEmail(email);
//
//        userService.bookedBook(book.getId(), foundUser.getEmail());
//
//        Book foundedBook = bookService.findBookById(book.getId());
//
//        userService.returnBook(book.getId(), foundUser.getEmail());
//
//        assertEquals(0, foundedBook.getAmount());
//    }
//
//    @Test
//    @DisplayName("Test return book that user took")
//    void testReturnBook() {
//        AppUser foundUser = userService.findByEmail(email);
//
//        userService.bookedBook(book.getId(), foundUser.getEmail());
//
//        userService.returnBook(book.getId(), foundUser.getEmail());
//
//        Book foundedBook = bookService.findBookById(book.getId());
//
//        assertEquals(1, foundedBook.getAmount());
//    }

    @Test
    @DisplayName("Test create password reset token for user and retrieve him by it")
    void createPasswordResetTokenAndRetrieveUserByItTest() {
        AppUser foundUser = userService.findByEmail(email);
        String token = "testToken";

        userService.createPasswordResetTokenForUser(foundUser, token);

        AppUser foundedUserByToken = userService.getUserByPasswordResetToken(token).get();

        assertEquals(foundUser, foundedUserByToken);
    }
}


























