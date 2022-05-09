package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByEmail(String email);
    void deleteAppUserByEmail(String email);
}
