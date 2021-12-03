package kpi.diploma.ovcharenko.service.book;

import kpi.diploma.ovcharenko.entity.Book;
import kpi.diploma.ovcharenko.entity.BookCategory;
import kpi.diploma.ovcharenko.repo.BookRepository;
import kpi.diploma.ovcharenko.repo.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class LibraryBookService implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public LibraryBookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
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
    public void updateBook(Book book, String category) {
        BookCategory bookCategory = new BookCategory(category);

        book.addCategory(bookCategory);

        bookRepository.save(book);
    }



    @Override
    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
    }

    @Override
    @Transactional
    public void addNewBook(Book book, String category) {
        BookCategory bookCategory = new BookCategory(category);

//        book.getCategories().add(bookCategory);

        book.addCategory(bookCategory);

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
    public Page<Book> getBookByCategory(Pageable pageable, String category) {
//        BookCategory bookCategory = categoryRepository.findFirstByCategory(category);
//
//        String categoryName = bookCategory.getCategory();
//
//        return bookRepository.findAllByCategoriesContaining(pageable, categoryName);

//        List<Integer> bookIds = categoryRepository.findAllBookIdByCategory(category);
//
//        Page<Book> books = null;
//        for (Integer id: bookIds) {
//            Page<Book> book = bookRepository.findById(id, pageable);
//            books.;
//        }

        return bookRepository.findByCategoryContains(category, pageable);
    }

    @Override
    public Set<String> findAllCategories() {
        return categoryRepository.findAllCategories();
    }

    @Override
    public Set<String> findBookCategories(Book book) {
        Set<BookCategory> categories = book.getCategories();

        Set<String> bookCategories = new HashSet<>();

        for (BookCategory category: categories) {
            bookCategories.add(category.getCategory());
        }

        return bookCategories;
    }
}


