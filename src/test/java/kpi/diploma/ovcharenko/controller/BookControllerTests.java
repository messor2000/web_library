package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import kpi.diploma.ovcharenko.service.book.BookService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("Test take all books by path /")
    public void getAllBooksTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test take all books with category test")
    public void getAllBooksByCategoryTest() throws Exception {
        this.mockMvc.perform(get("/test"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test take book by id")
    public void takeOneBookById() throws Exception {
        Book book = new Book("testBookForFind", 9999, "testAuthor", 1,
                "test1", "unused");
        BookCategory category = new BookCategory("categoryForTest");
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        bookService.addNewBook(book, category.getCategory(), file);
        long bookId = book.getId();

        this.mockMvc.perform(get("/find/" + bookId))
                .andDo(print())
                .andExpect(status().isOk());

        FileUtils.deleteDirectory(new File("/covers/" + "/" + file.getOriginalFilename()));
        bookService.deleteBookById(book.getId());
    }

    @Test
    @DisplayName("Test show add new book form without permission")
    public void showAddBookFormWithOutPermission() throws Exception {
        this.mockMvc.perform(get("/add"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Test show add new book form with role admin")
    public void showAddBookFormWithRoleAdmin() throws Exception {
        this.mockMvc.perform(get("/add"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test show update book form without permission")
    public void showUpdateFormWithoutPermission() throws Exception {
        Book book = new Book("testBookUpdate", 9999, "testAuthor", 1,
                "test1", "unused");
        BookCategory category = new BookCategory("categoryForTest");
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        bookService.addNewBook(book, category.getCategory(), file);
        long bookId = book.getId();

        this.mockMvc.perform(get("/edit/" + bookId))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        bookService.deleteBookById(bookId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Test show update book form with role admin")
    public void showUpdateFormWithRoleAdmin() throws Exception {
        Book book = new Book("testBookUpdate", 9999, "testAuthor", 1,
                "test1", "unused");
        BookCategory category = new BookCategory("categoryForTest");
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        bookService.addNewBook(book, category.getCategory(), file);
        long bookId = book.getId();

        this.mockMvc.perform(get("/edit/" + bookId))
                .andDo(print())
                .andExpect(status().isOk());

        bookService.deleteBookById(bookId);
    }
}
