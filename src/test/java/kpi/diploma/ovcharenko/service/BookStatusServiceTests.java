package kpi.diploma.ovcharenko.service;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookStatus;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.repo.BookStatusRepository;
import kpi.diploma.ovcharenko.service.book.BookService;
import kpi.diploma.ovcharenko.service.book.status.BookStatusService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class BookStatusServiceTests {

    @Autowired
    private BookStatusService bookStatusService;
    @Autowired
    private BookStatusRepository bookStatusRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;

    final Book book = new Book("test1", 9999, "testAuthor1", 2, "test1");

    @BeforeEach
    public void initEach() {
        bookService.addNewBook(book, null, null);
    }

    @AfterEach
    public void deleteEach() {
        bookRepository.deleteAll();
        bookStatusRepository.deleteAll();
    }

    @Test
    @DisplayName("should find all book statuses which contaminates two statuses 'FREE'")
    void findBookStatusesByBookTests() {
        List<BookStatus> bookStatusList = bookStatusService.findBookStatusesByBookId(book.getId());

        List<String> statuses = bookStatusList.stream()
                .map(BookStatus::getStatus)
                .collect(toList());

        assertThat(statuses).contains("FREE");
    }

    @Test
    @DisplayName("should successfully delete book status")
    void deleteBookStatusTest() {
        List<BookStatus> bookStatusList = bookStatusService.findBookStatusesByBookId(book.getId());
        BookStatus bookStatus = bookStatusList.get(0);
        bookStatusService.deleteBookStatus(bookStatus);

        List<BookStatus> bookStatusListAfterDeleting = bookStatusService.findBookStatusesByBookId(book.getId());

        assertEquals(bookStatusListAfterDeleting.size(), 1);
    }
}