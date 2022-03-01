package com.demo.archiver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.archiver.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);

	List<User> findByIsDeleted(boolean b);

	User findByIdAndIsDeleted(Long id, boolean b);

	List<User> findByFirstNameContainingOrLastNameContainingOrEmailContainingAndIsDeleted(String searchKey,
			String searchKey2, String searchKey3, boolean b);

	List<User> findByFirstNameContainingAndLastNameContainingAndIsDeleted(String firstName, String lastName, boolean b);

	User findByEmailAndIsDeleted(String username, boolean b);
}
