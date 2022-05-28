package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.user.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    VerificationToken findByUserId(Long userId);

    @Modifying
    @Query("delete from VerificationToken t where t.id = :id")
    void deleteVerificationTokenById(Long id);
}
