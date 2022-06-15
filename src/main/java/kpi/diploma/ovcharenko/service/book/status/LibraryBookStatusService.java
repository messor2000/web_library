package kpi.diploma.ovcharenko.service.book.status;

import kpi.diploma.ovcharenko.entity.book.BookStatus;
import kpi.diploma.ovcharenko.repo.BookStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryBookStatusService implements BookStatusService {

    private final BookStatusRepository bookStatusRepository;

    public LibraryBookStatusService(BookStatusRepository bookStatusRepository) {
        this.bookStatusRepository = bookStatusRepository;
    }

    @Override
    public List<BookStatus> findBookStatusesByBookId(Long bookId) {
        return bookStatusRepository.findAllByBookId(bookId);
    }

    @Override
    public void deleteBookStatusesByBookId(Long bookId) {
        bookStatusRepository.deleteBookStatusesByBookId(bookId);
    }

    @Override
    public void deleteBookStatus(BookStatus bookStatus) {
        bookStatusRepository.delete(bookStatus);
    }
}
