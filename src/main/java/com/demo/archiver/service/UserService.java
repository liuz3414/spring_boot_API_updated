package com.demo.archiver.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.demo.archiver.dto.UserRegistrationDto;
import com.demo.archiver.model.User;

public interface UserService extends UserDetailsService {
//	User save(UserRegistrationDto registrationDto);
	String registerUserAccount(UserRegistrationDto registrationDto);
	List<UserRegistrationDto> getAllUsers(String searchKey);
	UserRegistrationDto updateUser(Long id);
	String deleteUser(Long id);
	String updateUserAccount(UserRegistrationDto registrationDto, Long id);
	UserRegistrationDto findByEmail();
}
