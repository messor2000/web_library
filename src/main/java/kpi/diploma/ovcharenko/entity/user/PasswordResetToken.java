package kpi.diploma.ovcharenko.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "password_reset_token")
public class PasswordResetToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private AppUser user;

    private Date expiryDate;

    public PasswordResetToken(final String token, final AppUser user) {
        super();
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate();
    }

    private Date calculateExpiryDate() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, PasswordResetToken.EXPIRATION);
        return new Date(cal.getTime().getTime());
    }

    @SuppressWarnings("unused")
    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate();
    }
}
