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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class BookCardRepositoryTests {
    @Autowired
    private BookCardRepository bookCardRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserRepository userRepository;
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

//        userRepository.delete(user);
//        bookRepository.delete(book2);
    }



    @Test
    @DisplayName("should return list of book cards")
    void returnListOfBooks() {
        List<BookCard> bookCards = bookCardRepository.findAll();

        assertEquals(1, bookCards.size());
    }

}
