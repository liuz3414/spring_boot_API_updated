package com.demo.archiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.archiver.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

//    Optional<RoleEntity> findByName(RoleName roleUser);

	Role findByIdAndIsDeleted(Long roleId, boolean b);

	Role findTopByName(String string);

//	Role findByRoleName(String userRole);

}
