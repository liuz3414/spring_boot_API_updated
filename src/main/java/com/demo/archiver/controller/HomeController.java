package com.demo.archiver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.demo.archiver.dto.UserRegistrationDto;
import com.demo.archiver.model.User;
import com.demo.archiver.repository.UserRepository;
import com.demo.archiver.service.UserService;

@Controller
public class HomeController {

	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/")
	public String home(Model model, String searchKey) {
		List<UserRegistrationDto> returnValue = userService.getAllUsers(searchKey);
		model.addAttribute("listOfUsers",returnValue);
		return "index";
	}
	@GetMapping(path = "edit/{id}")
	public String updateUser(@PathVariable (name="id") Long id,Model model) {
		if(userRepository.findByIdAndIsDeleted(id, false)==null)
			return "redirect:/?userNotFoundSuccess";
		else {
		UserRegistrationDto returnValue = userService.updateUser(id);
		model.addAttribute("user",returnValue);
		return "updateUser";
		}
	}
	@GetMapping(path = "delete/{id}")
	public String deleteUser(@PathVariable (name="id") Long id) {
		User user = userRepository.findByIdAndIsDeleted(id, false);
		if(user==null) {
			return "redirect:/?userNotFoundSuccess";
		}
		else {
			userService.deleteUser(id);
			return "redirect:/?deleteSuccess";
		}
	}
	
	@PostMapping(path = "update/{id}")
	public String updateUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto, @PathVariable (name="id") Long id) {

         userService.updateUserAccount(registrationDto,id);  
		 return "redirect:/?updateSuccess";
	}
	
	@GetMapping(path = "detail")
	public String detail(Model model) {
        
		UserRegistrationDto returnValue = userService.findByEmail();
		model.addAttribute("detail",returnValue);
		return "index";
	}
	
}
