package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.book.BookTag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BookTagRepository extends CrudRepository<BookTag, Long> {
    Set<BookTag> findAll();
}
