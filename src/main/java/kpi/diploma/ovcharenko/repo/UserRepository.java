package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM AppUser u WHERE u.id = :id")
    void deleteUserById(@Param("id") Long id);
}
