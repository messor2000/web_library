package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookCategory;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookRepositoryTests {

    @Autowired
    private BookRepository repository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Test save list of books")
    public void saveListOfBooks() {
        List<Book> books = Arrays.asList(
                new Book("testBook1", 9999, "testAuthor1", 1, "test1",
                        "unused"),
                new Book("testBook2", 9999, "testAuthor2", 1, "test2",
                        "unused")
        );
        Iterable<Book> allBooks = repository.saveAll(books);

        AtomicInteger validIdFound = new AtomicInteger();
        allBooks.forEach(book -> {
            if (book.getId() > 0) {
                validIdFound.getAndIncrement();
            }

            repository.delete(book);
        });

        assertThat(validIdFound.intValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test find book by name")
    public void findBookByName() {
        Book book = new Book("testBookForFinding1", 9999, "testAuthor1", 1,
                "test1", "unused");

        repository.save(book);

        Pageable findOneByName = PageRequest.of(0, 1);
        Page<Book> searchedBook = repository.findByBookName(book.getBookName(), findOneByName);

        repository.delete(searchedBook.get().findFirst().get());

        assertEquals(searchedBook.get().findFirst().get(), book);
    }

    @Test
    @DisplayName("Test find book with category")
    public void findBookCategoryContains() {
        BookCategory category = new BookCategory();
        List<Book> books = Arrays.asList(
                new Book("testBookForShowingCategory", 9999, "testAuthor1", 1,
                        "test1", "unused"),
                new Book("testBookForShowingCategory2", 9999, "testAuthor1", 1,
                        "test1", "unused")
        );

        for (Book book: books) {
            category = new BookCategory("test1");
            book.addCategory(category);
            repository.save(book);
        }

        List<Book> booksWithCategory = repository.findByCategoryContains("test1", PageRequest.of(0, 20)).toList();

        categoryRepository.delete(category);
        repository.deleteAll(booksWithCategory);

        assertEquals(books.get(0).getBookName(), booksWithCategory.get(0).getBookName());
    }
}
