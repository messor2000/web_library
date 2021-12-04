package kpi.diploma.ovcharenko.entity;

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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
@Table(name = "books")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "idbooks", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
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

    @Lob
    @Column(name = "image", columnDefinition="longblob")
    private byte[] image;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<BookCategory> categories = new HashSet<>();

    public void addCategory(BookCategory category){
        categories.add(category);
        category.setBook(this);
    }
}
