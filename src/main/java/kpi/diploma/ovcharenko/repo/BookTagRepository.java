package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.book.BookTag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BookTagRepository extends CrudRepository<BookTag, Long> {
    Set<BookTag> findAll();

    void deleteBookTagByBook(Book book);

    boolean existsBookTagByTagName(String tag);

    boolean existsBookTagByTagNameAndBook(String tag, Book book);

    @Query("SELECT b FROM BookTag b WHERE b.tagName IN :tags")
    Set<BookTag> findByTags(@Param("tags") Set<String> tags);
}
