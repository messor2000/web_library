package kpi.diploma.ovcharenko.entity.user;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "role")
public class UserRole {
    @Id
    @Column(name = "idrole")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public UserRole(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserRole userRole = (UserRole) o;
        return id != null && Objects.equals(id, userRole.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
