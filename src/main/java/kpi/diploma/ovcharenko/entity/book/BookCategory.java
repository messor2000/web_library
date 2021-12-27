package kpi.diploma.ovcharenko.entity.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "book_categories")
public class BookCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcategory", insertable = false, updatable = false)
    private Long id;

    @Column(name = "category")
    private String category;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Book book;

    public BookCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookCategory )) return false;
        return id != null && id.equals(((BookCategory) o).getId());
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
