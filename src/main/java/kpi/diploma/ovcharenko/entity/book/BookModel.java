package kpi.diploma.ovcharenko.entity.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookModel implements Serializable {

    private Long id;
    private String bookName;
    private int year;
    private String author;
    private int amount;
    private String description;
    private String bookStatus;
    private byte[] image;
    private transient Set<BookCategory> categories;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookModel bookModel = (BookModel) o;
        return year == bookModel.year &&
                amount == bookModel.amount &&
                Objects.equals(id, bookModel.id) &&
                Objects.equals(bookName, bookModel.bookName) &&
                Objects.equals(author, bookModel.author) &&
                Objects.equals(description, bookModel.description) &&
                Objects.equals(bookStatus, bookModel.bookStatus) &&
                Arrays.equals(image, bookModel.image) &&
                Objects.equals(categories, bookModel.categories);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, bookName, year, author, amount, description, bookStatus, categories);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    public Long getId() {
        return id;
    }

    public BookModel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public BookModel setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
        return this;
    }

    public String getBookName() {
        return bookName;
    }

    public BookModel setBookName(String bookName) {
        this.bookName = bookName;
        return this;
    }

    public int getYear() {
        return year;
    }

    public BookModel setYear(int year) {
        this.year = year;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public BookModel setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Set<BookCategory> getCategories() {
        return categories;
    }

    public BookModel setCategories(Set<BookCategory> categories) {
        this.categories = categories;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public BookModel setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public BookModel setDescription(String description) {
        this.description = description;
        return this;
    }
}
