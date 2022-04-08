package kpi.diploma.ovcharenko.entity.user;

import kpi.diploma.ovcharenko.entity.book.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"), name = "library_user")
public class AppUser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @NotEmpty
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty
    @Column(name = "last_name")
    private String lastName;

    @NotEmpty
    @Email
    @Column(name = "email")
    private String email;

    @NotEmpty
    @Column(name = "password")
    private String password;

    @CreationTimestamp
    @Column(name = "create_time")
    private Timestamp registrationDate;

    private String resetPasswordToken;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<UserRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Book> books = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser user = (AppUser) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, password);
    }

    public void addBook(Book book){
        books.add(book);
        book.setUser(this);
    }
    public void removeBook(Book book){
        books.remove(book);
        book.setUser(null);
    }

    public void setRoles(Collection<UserRole> roles) {
        this.roles = roles;
    }
}

