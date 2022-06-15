package kpi.diploma.ovcharenko.entity.card;

import kpi.diploma.ovcharenko.entity.book.Book;
import kpi.diploma.ovcharenko.entity.user.AppUser;
import kpi.diploma.ovcharenko.util.PostgreSQLEnumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "booking_card")
//@TypeDef(
//        name = "pgsql_enum",
//        typeClass = PostgreSQLEnumType.class
//)
public class BookCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "status")
//    @Enumerated(EnumType.STRING)
//    @Type(type = "pgsql_enum")
    private String cardStatus;

    @CreationTimestamp
    @Column(name = "create_time")
    private Timestamp createdTime;

    @SuppressWarnings("unused")
    public LocalDate returnCreationDateTime() {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d/MM/uuuu");
        String text = createdTime.toLocalDateTime().format(formatters);

        return LocalDate.parse(text, formatters);
    }

    @SuppressWarnings("unused")
    public String calculateExistedTime() {
        LocalDateTime localDateTime = createdTime.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(now, localDateTime);
        long d = (duration.toDays()) * -1;
        long h = (duration.toHours() - 24 * d) * -1;

        if (h > 24) {
            return d + "d";
        }

        return h + "h";
    }
}
