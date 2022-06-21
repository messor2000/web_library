package kpi.diploma.ovcharenko.controller;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.service.book.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import static kpi.diploma.ovcharenko.controller.PageableAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTests {

    @Mock
    private MockMvc mockMvc;
    @Mock
    BookService bookService;
    @Mock
    BookRepository bookRepository;

    private final String category = "forTest";

    final Book book = new Book("test1", 9999, "testAuthor1", 1, "test1");

    @BeforeEach
    public void initEach() {
        bookService.addNewBook(book, category, "not used");
    }

    @AfterEach
    public void deleteEach() {
        bookRepository.deleteAll();
    }

    @Test
    void getAllBooksTest() throws Exception {
        mockMvc.perform(get("/")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(bookRepository).findAll(pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertThat(pageable).hasPageNumber(1);
        assertThat(pageable).hasPageSize(20);
    }
}
