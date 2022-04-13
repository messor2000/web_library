package kpi.diploma.ovcharenko.service;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.entity.user.UserModel;
import kpi.diploma.ovcharenko.repo.UserRepository;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AppUserServiceTests {

    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserRepository userRepository;

    private Book book = new Book();

//    @Before
//    public void setUpBook() {
//        book = new Book("testBookCategory", 9999, "testAuthor", 1,
//                "test1", "unused");
//        BookCategory category = new BookCategory("categoryForTest");
//        MockMultipartFile file
//                = new MockMultipartFile(
//                "file",
//                "hello.txt",
//                MediaType.IMAGE_JPEG_VALUE,
//                "Hello, World!".getBytes()
//        );
//
//        bookService.addNewBook(book, category.getCategory(), file);
//    }

    @After
    public void deleteBook() {
        bookService.deleteBookById(book.getId());
    }

    @Test
    @DisplayName("Test save new user and find him by email")
    public void saveAndRetrieveNewUserByEmail() {
        UserModel userModel = new UserModel().toBuilder()
                .firstName("forTest")
                .lastName("forTest")
                .email("forTest@gmail.com")
                .password("forTest")
                .build();

        userService.save(userModel);

        AppUser foundUser = userService.findByEmail(userModel.getEmail());

        userRepository.delete(foundUser);

        assertEquals(foundUser.getEmail(), userModel.getEmail());
    }

//    @Test
//    @DisplayName("Test user take book, and in book field userid shows user owner")
//    @Transactional
//    public void userTakeBookTest() {
//        UserModel userModel = new UserModel().toBuilder()
//                .firstName("forTestTakingBook")
//                .lastName("forTest")
//                .email("forTest@gmail.com")
//                .password(passwordEncoder.encode("forTest"))
//                .build();
//
//       userService.save(userModel);
//
//        AppUser user = userService.findByEmail(userModel.getEmail());
//
//        userService.takeBook(book.getId(), user.getEmail());
//
//        userRepository.delete(user);
//
//        Book foundBook = bookService.findBookById(book.getId());
//        assertEquals(user.getEmail(), foundBook.getUser().getEmail());
//    }
//
//    @Test
//    @DisplayName("Test user return book, and in book field userid must be null")
//    @Transactional
//    public void userReturnBookTest() {
//        UserModel userModel = new UserModel().toBuilder()
//                .firstName("forTestTakingBook")
//                .lastName("forTest")
//                .email("forTest@gmail.com")
//                .password(passwordEncoder.encode("forTest"))
//                .build();
//
//        AppUser user = userService.save(userModel);
//
//        AppUser user = userService.findByEmail(userModel.getEmail());
//
//        userService.returnBook(book.getId(), user.getEmail());
//
//        userRepository.delete(user);
//
//        Book foundBook = bookService.findBookById(book.getId());
//        assertNull(foundBook.getUser());
//    }
}


























