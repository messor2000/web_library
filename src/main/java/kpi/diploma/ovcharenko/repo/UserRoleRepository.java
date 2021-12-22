package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    UserRole findByName(String name);

    Set<UserRole> findAllByName(String name);
}
