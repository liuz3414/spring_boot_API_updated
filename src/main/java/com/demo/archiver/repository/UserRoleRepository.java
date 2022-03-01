package com.demo.archiver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.archiver.model.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	List<UserRole> findByUserIdAndIsDeleted(Long id, boolean b);

	UserRole findTopByUserIdAndIsDeleted(Long id, boolean b);

	UserRole findTopByRoleIdAndIsDeleted(Long id, boolean b);

}
