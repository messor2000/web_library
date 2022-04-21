package kpi.diploma.ovcharenko.entity.book.status;

import kpi.diploma.ovcharenko.entity.book.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "book_status")
public class BookStatus {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="book_id", nullable=false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(length = 45, name = "book_status")
    private Status status;

    public BookStatus(Book book, Status status) {
        this.book = book;
        this.status = status;
    }

    public BookStatus(Status status) {
        this.status = status;
    }
}
