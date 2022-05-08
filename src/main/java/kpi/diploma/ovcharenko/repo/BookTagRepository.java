package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookTag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookTagRepository extends CrudRepository<BookTag, Long> {
    Set<BookTag> findAll();

    Optional<BookTag> findBookTagByTagName(String tagName);

    Set<BookTag> findByBooks_Id(Long bookId);

    Set<BookTag> findAllByBooksIn(Collection<Book> books);
}
