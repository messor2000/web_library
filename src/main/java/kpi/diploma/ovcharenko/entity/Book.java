package kpi.diploma.ovcharenko.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
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

    @Column(name = "subjects")
    @NotBlank(message = "Year is mandatory")
    private String subject;

    @Column(name = "amount")
    private int amount;

    @Lob
    @Column(name = "image", columnDefinition="longblob")
    private byte[] image;

}
