package kpi.diploma.ovcharenko.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookModel implements Serializable {

    private String bookName;
    private int year;
    private String author;
    private String subject;
    private int amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookModel bookModel = (BookModel) o;
        return year == bookModel.year &&
                Objects.equals(bookName, bookModel.bookName) &&
                Objects.equals(author, bookModel.author) &&
                Objects.equals(subject, bookModel.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookName, year, author, subject, amount);
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

    public String getSubject() {
        return subject;
    }

    public BookModel setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public BookModel setAmount(int amount) {
        this.amount = amount;
        return this;
    }
}
