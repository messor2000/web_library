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
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
    @NotBlank(message = "Year is mandatory")
    @Size(min = 4, max = 4, message = "Year should be like 1984")
    private int year;

    @Column(name = "author")
    @NotBlank(message = "Year is mandatory")
    private String author;

    @Column(name = "amount")
    private int amount;

    @Column(name = "description")
    private String description;

//    @Lob
//    @Column(name = "image", columnDefinition="longblob")
//    private Byte[] image;

    @Column(name = "image", length = 64)
    private String image;

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

    @Transient
    public String getCoverImagePath() {
        if (image == null || id == null) return null;

        return "/covers/" + id + "/" + image;
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
                Objects.equals(image, book.image) &&
                Objects.equals(bookStatus, book.bookStatus) &&
                Objects.equals(categories, book.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookName, year, author, amount, description, image, bookStatus, categories);
    }
}
