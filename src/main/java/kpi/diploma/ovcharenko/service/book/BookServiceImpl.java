package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.util.Paged;
import kpi.diploma.ovcharenko.util.Paging;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import kpi.diploma.ovcharenko.repo.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Aleksandr Ovcharenko
 */
@Service
public class BookServiceImpl implements IBookService, IPagingService {

    @Autowired
    BookRepository bookRepository;

    @Override
    public List<Book> getAllBook() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> getAllBooksByNameAsc() {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "bookName");

        return bookRepository.findAll(Sort.by(order));
    }

    @Override
    public List<Book> getAllBookByCategory(String subject) {
        return bookRepository.findAllBySubject(subject);
    }

    @Override
    public Book getByName(String name) {
        return bookRepository.getByBookName(name);
    }

    @Override
    public Paged<Book> getPage(int pageNumber, int size) {
//        PageRequest request = PageRequest.of(pageNumber - 1, size, new Sort(Sort.Direction.ASC, "id"));

        PageRequest request = PageRequest.of(pageNumber - 1, size);
        Page<Book> postPage = bookRepository.findAll(request);
        return new Paged<>(postPage, Paging.of(postPage.getTotalPages(), pageNumber, size));
    }
}
