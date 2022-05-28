package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.user.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUserId(Long userId);

    @Modifying
    @Query("delete from PasswordResetToken t where t.id = :id")
    void deletePasswordResetTokenById(Long id);
}