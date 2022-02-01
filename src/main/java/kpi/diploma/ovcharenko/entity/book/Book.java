package kpi.diploma.ovcharenko.entity.book;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
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
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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

    @Column(name = "book_status")
    private String bookStatus;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<BookCategory> categories = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private AppUser user;

    public void addCategory(BookCategory category){
        categories.add(category);
        category.setBook(this);
    }

    public void removeCategory(BookCategory category) {
        categories.remove(category);
        category.setBook(null);
    }

    public static Book of(Long id, String bookName, int year, String author, int amount, String description,
                          String bookStatus, Set<BookCategory> categories) {
        return Book.builder()
                .id(id)
                .bookName(bookName)
                .year(year)
                .author(author)
                .amount(amount)
                .description(description)
                .bookStatus(bookStatus)
                .categories(categories)
                .build();
    }

    public static Book fromModel(BookModel bookModel) {
        return Book.of(bookModel.getId(), bookModel.getBookName(), bookModel.getYear(), bookModel.getAuthor(),
                bookModel.getAmount(), bookModel.getDescription(), bookModel.getBookStatus(), bookModel.getCategories());
    }

    public BookModel toDto() {
        return BookModel.of(id, bookName, year, author, amount, description, bookStatus, categories);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return year == book.year &&
                amount == book.amount &&
                Objects.equals(id, book.id) &&
                Objects.equals(bookName, book.bookName) &&
                Objects.equals(author, book.author) &&
                Objects.equals(description, book.description) &&
                Objects.equals(bookStatus, book.bookStatus) &&
                Objects.equals(categories, book.categories) &&
                Objects.equals(user, book.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookName, year, author, amount, description, bookStatus, categories, user);
    }
}
