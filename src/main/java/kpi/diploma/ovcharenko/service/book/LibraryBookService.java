package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.repo.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class LibraryBookService implements BookService {

    private final BookRepository bookRepository;

    public LibraryBookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void deleteBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        bookRepository.delete(book);
    }

    @Override
    @Transactional
    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
    }

    @Override
    @Transactional
    public void addNewBook(Book book) {
//        Book book1 = new Book();
//        book1.setBookName(book.getBookName());
//        book1.setAuthor(book.getAuthor());
//        book1.setYear(book.getYear());
//        book1.setSubject(book.getSubject());
//        book1.setAmount(book.getYear());
//        book1.setImage();

        bookRepository.save(book);
    }

    @Override
    public Page<Book> findBookByName(String name) {
        Pageable findOneByName = PageRequest.of(0, 1);

        return bookRepository.findByBookName(name, findOneByName);
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<Book> getSortingBooksByYear(Pageable pageable) {
        Pageable sortedByPriceDesc = PageRequest.of(1, 20, Sort.by("year").descending());

        return bookRepository.findAll(sortedByPriceDesc);
    }

    @Override
    public Page<Book> getSortingBooksAlphabetical(Pageable pageable) {
        Pageable sortedByPriceDesc = PageRequest.of(1, 20, Sort.by("bookName").ascending());

        return bookRepository.findAll(sortedByPriceDesc);
    }

    @Override
    public Page<Book> getBooksSorted(Pageable pageable, String sort) {
        Pageable sorted = null;

        if (sort.equals("Alphabetical")) {
            sorted = PageRequest.of(1, 20, Sort.by("bookName").ascending());
        } else if (sort.equals("Published")) {
            sorted =  PageRequest.of(1, 20, Sort.by("year").descending());
        }

        assert sorted != null;
        return bookRepository.findAll(sorted);
    }

    @Override
    public Page<Book> getBookByCategory(Pageable pageable, String subject) {
        return bookRepository.findBySubjectContains(pageable, subject);
    }
}


