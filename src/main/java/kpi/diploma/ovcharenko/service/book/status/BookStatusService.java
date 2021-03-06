package kpi.diploma.ovcharenko.service.book.status;

import kpi.diploma.ovcharenko.entity.book.BookStatus;

import java.util.List;

public interface BookStatusService {

    List<BookStatus> findBookStatusesByBookId(Long bookId);

    void deleteBookStatus(BookStatus bookStatus);
}
