package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.user.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    VerificationToken findByUserId(Long userId);
}
