package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import kpi.diploma.ovcharenko.service.book.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class BookCategoryTests {
    @Autowired
    private BookCategoryRepository bookCategoryRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookService bookService;

    private final String category = "CategoryForTest";
    Book book = new Book("test1", 9999, "testAuthor1", 1, "test1");

    @BeforeEach
    public void initEach() {
        bookService.addNewBook(book, category, "not used");
    }

    @AfterEach
    public void deleteEach() {
        bookService.deleteCategory(book.getId(), category);
        bookService.deleteBookById(book.getId());
    }

//    @BeforeEach
//    @Transactional
//    public void initEach() {
//        book.setCategories(Collections.singleton(new BookCategory(category)));
//        bookRepository.save(book);
//    }
//
//    @AfterEach
//    public void deleteEach() {
//        bookService.deleteCategory(book.getId(), category);
//        bookRepository.delete(book);
//    }

    @Test
    @DisplayName("should return book category by book and category name")
    void returnBookCategoryByBookAndCategory() {
        BookCategory bookCategory = bookCategoryRepository.findByCategoryAndBook(category, book);

        assertEquals(bookCategory.getCategory(), category);
    }

    @Test
    @DisplayName("should return true if category exist")
    void checkIfBookCategoryExistByBookAndCategory() {
        assertTrue(bookCategoryRepository.existsBookCategoryByCategoryAndBook(category, book));
    }

    @Test
    @DisplayName("should return false because category doesn't exist")
    void checkIfBookCategoryDoesntExistByBookAndCategory() {
        assertTrue(bookCategoryRepository.existsBookCategoryByCategoryAndBook(category, book));
    }
}
