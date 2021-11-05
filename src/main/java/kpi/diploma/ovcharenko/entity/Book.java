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
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Aleksandr Ovcharenko
 */
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
    private String bookName;

    @Column(name = "year")
    private int year;

    @Column(name = "author")
    private String author;

    @Column(name = "subjects")
    private String subject;

    @Column(name = "amount")
    private int amount;
}
