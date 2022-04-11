package kpi.diploma.ovcharenko.service;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import kpi.diploma.ovcharenko.service.book.BookService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookServiceTests {

    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("Test get all pageable books")
    public void getAllPageableBooksTest() {
        Page<Book> bookList = bookService.getAllBooks(PageRequest.of(1, 20));
        assertThat(bookList.getTotalElements()).isPositive();
    }

    @Test
    @DisplayName("Test get all pageable books by category")
    public void getAllPageableBooksWithOneCategoryTest() {
        Page<Book> bookList = bookService.getBookByCategory(PageRequest.of(1, 20), "test");
        assertThat(bookList.getTotalElements()).isOne();
    }

    @Test
    @DisplayName("Test get all book categories")
    public void getBookCategoriesTest() {
        Book book = new Book("testBookCategory", 9999, "testAuthor", 1,
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

        Set<String> categorySet = bookService.findBookCategories(book);

        bookService.deleteBookById(book.getId());

        assertTrue(categorySet.contains(category.getCategory()));
    }

    @Test
    @DisplayName("Test get all categories")
    public void getCategoriesTest() {
        Book book = new Book("testBookCategory", 9999, "testAuthor", 1,
                "test1", "unused");
        BookCategory category = new BookCategory("categoryForTestAmountOfCategories");
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        Set<String> categoriesSetBefore = bookService.findAllCategories();

        bookService.addNewBook(book, category.getCategory(), file);

        Set<String> categoriesSetAfter = bookService.findAllCategories();

        bookService.deleteBookById(book.getId());

        assertEquals(categoriesSetBefore.size() + 1, categoriesSetAfter.size());
    }

    @Test
    @DisplayName("Test delete category from book and after that category deletes if no book with this category")
    public void deleteCategoryFromBook() {
        Book book = new Book("testBookCategory", 9999, "testAuthor", 1,
                "test1", "unused");
        BookCategory category = new BookCategory("categoryForTestThatShowDeleted");
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        bookService.addNewBook(book, category.getCategory(), file);

        bookService.deleteCategory(book.getId(), category.getCategory());

        Book bookAfterDeletedCategory = bookService.findBookById(book.getId());

        bookService.deleteBookById(book.getId());

        assertThat(bookAfterDeletedCategory.getCategories().isEmpty());
        assertFalse(bookService.findAllCategories().contains(category.getCategory()));
    }

    @Test
    @SneakyThrows
    @DisplayName("Test adding new book")
    public void addNewBookTest() {
        Book book = new Book("testNewBook", 9999, "testAuthor", 1,
                "test1", "unused");
        BookCategory category = new BookCategory("test1");
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        bookService.addNewBook(book, category.getCategory(), file);

        Book foundBook = bookService.findBookById(book.getId());

        FileUtils.deleteDirectory(new File("/covers/" + "/" + file.getOriginalFilename()));

        bookService.deleteBookById(book.getId());

        assertEquals(book, foundBook);
    }

    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Test deleting added book")
    public void deleteBookByIdTest() {
        Book book = new Book("testBookForDeleting", 9999, "testAuthor", 1,
                "test1", "unused");
        BookCategory category = new BookCategory("test1");
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.IMAGE_JPEG_VALUE,
                "Hello, World!".getBytes()
        );

        bookService.addNewBook(book, category.getCategory(), file);

        deleteTestFile(new File("/covers/" + book.getId() + "/hello.txt"));
        bookService.deleteBookById(book.getId());

        bookService.findBookById(book.getId());
    }

//    @Test
//    @SneakyThrows
//    @DisplayName("Test update books name")
//    public void updateBookByNameTest() {
//        Book book = new Book("testBookForCheckId", 9999, "testAuthor", 1,
//                "test1", "unused");
//        BookCategory category = new BookCategory("test1");
//        MockMultipartFile file
//                = new MockMultipartFile(
//                "file",
//                "hello.txt",
//                MediaType.IMAGE_JPEG_VALUE,
//                "Hello, World!".getBytes()
//        );
//
//        bookService.addNewBook(book, category.getCategory(), file);
//        String newBookName = "newBookNameForTest";
//        book.setBookName(newBookName);
//        bookService.updateBook(book, category.getCategory(), file);
//
//        Book foundBook = bookService.findBookById(book.getId());
//
//        FileUtils.deleteDirectory(new File("/covers/" + "/" + file.getOriginalFilename()));
//
//        bookService.deleteBookById(book.getId());
//
//        assertEquals(newBookName, foundBook.getBookName());
//    }

    private void deleteTestFile(File file) {
        try {
            boolean success = file.delete();
            if (!success) {
                log.error("File wasn't deleted");
            }
        } catch (SecurityException ex) {
            log.error("Error trying delete file", ex);
        }
    }

}
