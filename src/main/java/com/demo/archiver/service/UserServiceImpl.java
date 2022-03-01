package com.demo.archiver.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.archiver.dto.UserRegistrationDto;
import com.demo.archiver.model.Role;
import com.demo.archiver.model.User;
import com.demo.archiver.model.UserRole;
import com.demo.archiver.repository.RoleRepository;
import com.demo.archiver.repository.UserRepository;
import com.demo.archiver.repository.UserRoleRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

//	@Override
//	public User save(UserRegistrationDto registrationDto) {
//		User user = new User(registrationDto.getFirstName(), registrationDto.getLastName(), registrationDto.getEmail(),
//				passwordEncoder.encode(registrationDto.getPassword()), Arrays.asList(new Role("ROLE_USER")));
//
//		return userRepository.save(user);
//	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailAndIsDeleted(username,false);			
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		List<UserRole> userRoles = userRoleRepository.findByUserIdAndIsDeleted(user.getId(),false);
		List<Role> roles = new ArrayList<>();
		for(UserRole userRole:userRoles) {
			Role role = roleRepository.findByIdAndIsDeleted(userRole.getRoleId(),false);
			roles.add(role);
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(), mapRolesToAuthorities(roles));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	@Override
	public String registerUserAccount(UserRegistrationDto registrationDto) {
		User userEntity = new User();
		BeanUtils.copyProperties(registrationDto, userEntity);
		userEntity.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
		User savedUserEntity = userRepository.save(userEntity);
		
		UserRole userRoleEntity = new UserRole();
		Role roleEntity=null;
		if(!"".equals(registrationDto.getRoleName()) && registrationDto.getRoleName()!=null) {
		  roleEntity = roleRepository.findTopByName(registrationDto.getRoleName());
		if(roleEntity!=null) {
			userRoleEntity.setRoleId(roleEntity.getId());
			
		}
		else {
			roleEntity = new Role();
			roleEntity.setName(registrationDto.getRoleName());
			Role savedRoleEntity = roleRepository.save(roleEntity);
			userRoleEntity.setRoleId(savedRoleEntity.getId());
		}
		}
	    else {
			
			roleEntity = roleRepository.findTopByName("User");
			if(roleEntity!=null) {
				userRoleEntity.setRoleId(roleEntity.getId());
				
			}
			else {
				roleEntity = new Role();
				roleEntity.setName("User");
				Role savedRoleEntity = roleRepository.save(roleEntity);
				userRoleEntity.setRoleId(savedRoleEntity.getId());
			}
		  }
		userRoleEntity.setUserId(savedUserEntity.getId());
       userRoleRepository.save(userRoleEntity);
		
		return "saved";
	}

	@Override
	public List<UserRegistrationDto> getAllUsers(String searchKey) {
		List<UserRegistrationDto> returnValue = new ArrayList<>();
		List<User> users = new ArrayList<>();


		if(searchKey==null)
			users=userRepository.findByIsDeleted(false);
		else {
			 searchKey = searchKey.trim();
				String[] searchKeyArray = searchKey.split(" ");
			if(searchKeyArray.length == 1)
				 users = userRepository.findByFirstNameContainingOrLastNameContainingOrEmailContainingAndIsDeleted(searchKey,searchKey,searchKey,false);
			 else if (searchKeyArray.length == 2) {
					String firstName = searchKeyArray[0];
					String lastName = searchKeyArray[1];
					users = userRepository.findByFirstNameContainingAndLastNameContainingAndIsDeleted(firstName,
							lastName, false);
		}
		  
		}
		for(User user:users) {
			UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
			BeanUtils.copyProperties(user, userRegistrationDto);
			returnValue.add(userRegistrationDto);
		}
		return returnValue;
	}

	@Override
	public UserRegistrationDto updateUser(Long id) {
		UserRegistrationDto returnValue = new UserRegistrationDto();
		User user = userRepository.findByIdAndIsDeleted(id,false);
		BeanUtils.copyProperties(user, returnValue);
		UserRole userRole = userRoleRepository.findTopByUserIdAndIsDeleted(user.getId(), false);
		if(userRole!=null) {
			Role role = roleRepository.findByIdAndIsDeleted(userRole.getRoleId(), false);
			if(role!=null)
				returnValue.setRoleName(role.getName());
		}
		return returnValue;
	}

	@Override
	public String deleteUser(Long id) {
       User user = userRepository.findByIdAndIsDeleted(id,false);	
		user.setDeleted(true);
		userRepository.save(user);
		return "deleted";
	}

	@Override
	public String updateUserAccount(UserRegistrationDto registrationDto, Long id) {
		User userEntity = userRepository.findByIdAndIsDeleted(id, false);
		userEntity.setFirstName(registrationDto.getFirstName());
		userEntity.setLastName(registrationDto.getLastName());
		User savedUserEntity = userRepository.save(userEntity);
		return "updated";
	}

	@Override
	public UserRegistrationDto findByEmail() {
		UserRegistrationDto returnValue = new UserRegistrationDto();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.findByEmail(auth.getName());
		BeanUtils.copyProperties(user, returnValue);
		return returnValue;
	}


}
