package kpi.diploma.ovcharenko.entity.book;

import kpi.diploma.ovcharenko.entity.book.status.BookStatus;
import kpi.diploma.ovcharenko.entity.card.BookCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idbooks", insertable = false, updatable = false)
    private Long id;

    @Column(name = "book_name")
    @NotBlank(message = "Book name is mandatory")
    private String bookName;

    @Column(name = "year")
    private int year;

    @Column(name = "author")
    private String author;

    @Column(name = "amount")
    private int amount;

    @Column(name = "description")
    private String description;

    @Column(name = "section")
    private String section;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private Set<BookStatus> statuses = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    private Set<BookCategory> categories = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    private Set<BookCard> bookCards = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
            })
    @JoinTable(
            name = "books_tags",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "book_tag_id")}
    )
    Set<BookTag> tags = new HashSet<>();

    public void addCategory(BookCategory category) {
        categories.add(category);
        category.setBook(this);
    }

    public void removeCategory(BookCategory category) {
        categories.remove(category);
        category.setBook(null);
    }

    public void setStatus(BookStatus status) {
        statuses.add(status);
        status.setBook(this);
    }

    public Book(@NotBlank(message = "Book name is mandatory") String bookName, int year, String author,
                int amount, String description) {
        this.bookName = bookName;
        this.year = year;
        this.author = author;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return year == book.year && amount == book.amount && Objects.equals(id, book.id) && Objects.equals(bookName, book.bookName) && Objects.equals(author, book.author) && Objects.equals(description, book.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookName, year, author, amount, description);
    }
}
