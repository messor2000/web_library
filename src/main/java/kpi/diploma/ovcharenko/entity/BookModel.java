package kpi.diploma.ovcharenko.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Aleksandr Ovcharenko
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
public class BookModel implements Serializable {
    private String bookName;
    private int year;
    private String author;
    private String subject;
    private int amount;
}
