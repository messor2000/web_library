package kpi.diploma.ovcharenko.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "verification_token")
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_VERIFY_USER"))
    private AppUser user;

    private Date expiryDate;

    public VerificationToken(final String token, final AppUser user) {
        super();
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate();
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(final AppUser user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(final Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    private Date calculateExpiryDate() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, VerificationToken.EXPIRATION);
        return new Date(cal.getTime().getTime());
    }

    @SuppressWarnings("unused")
    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate();
    }
}
