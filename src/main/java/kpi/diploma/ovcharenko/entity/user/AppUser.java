package kpi.diploma.ovcharenko.entity.user;

import kpi.diploma.ovcharenko.entity.card.BookCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"), name = "library_user")
public class AppUser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Column(name = "number")
    private String telephoneNumber;

    @NotEmpty
    @Column(name = "password")
    private String password;

    @CreationTimestamp
    @Column(name = "create_time")
    private Timestamp registrationDate;

    @Column(name = "enabled")
    private boolean enabled;

    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<UserRole> roles = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<BookCard> bookCards = new HashSet<>();

    public void addBookCard(BookCard bookCard) {
        bookCards.add(bookCard);
        bookCard.setUser(this);
    }

    public void setRoles(Collection<UserRole> roles) {
        this.roles = roles;
    }
}

