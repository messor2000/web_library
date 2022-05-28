package kpi.diploma.ovcharenko.repo;

import kpi.diploma.ovcharenko.entity.user.UserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
}
