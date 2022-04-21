package kpi.diploma.ovcharenko.entity.book;

import com.amazonaws.services.dynamodbv2.xspec.B;
import kpi.diploma.ovcharenko.entity.book.status.BookStatus;
import kpi.diploma.ovcharenko.entity.card.BookingCard;
import kpi.diploma.ovcharenko.entity.card.TakenBookCard;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.List;
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

//    @Column(name = "book_status")
//    private String bookStatus;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<BookStatus> statuses = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<BookCategory> categories = new HashSet<>();

    @ManyToMany(mappedBy = "books")
    List<AppUser> users;

    @OneToMany(mappedBy = "book")
    private Set<BookingCard> bookingCards;

    @OneToMany(mappedBy = "book")
    private Set<TakenBookCard> takenBookCards;

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

    public void setStatuses(Set<BookStatus> statuses) {
        for (BookStatus bookStatus: statuses) {
            statuses.add(bookStatus);
            bookStatus.setBook(this);
        }
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
