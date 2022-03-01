package com.demo.archiver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.demo.archiver.dto.UserRegistrationDto;
import com.demo.archiver.model.User;
import com.demo.archiver.repository.UserRepository;
import com.demo.archiver.service.UserService;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

	@Autowired
	UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
//    @Autowired
//    AuthenticationManager authenticationManager;
//    
	@Autowired
	UserRepository userRepository;

	public RegistrationController(UserService userService) {
		super();
		this.userService = userService;
	}

	@ModelAttribute("user")
	public UserRegistrationDto userRegistrationDto() {
		return new UserRegistrationDto();
	}

	@GetMapping
	public String showRegistrationForm() {
		return "registration";
	}

	@PostMapping
	public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
		
		if(userRepository.findByEmail(registrationDto.getEmail())!=null) {
	          Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	          if("anonymousUser".equals(auth.getName()))
	              return "redirect:/registration?userwithThisEmailFoundError";
	          else
	         	 return "redirect:/?userwithThisEmailFoundError";
		}
		else {
	          userService.registerUserAccount(registrationDto);
	          Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	          if("anonymousUser".equals(auth.getName()))
	              return "redirect:/registration?success";
	          else
	         	 return "redirect:/?successes";
			
		}
		
		

//		userService.save(registrationDto);
//		return "redirect:/registration?success";
	}
	
//    @PostMapping("/logins")	
//    public UserRegistrationDto authenticateUser(@ModelAttribute("user") LoginRequestModel loginRequest) {
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getEmail(),
//                        loginRequest.getPassword()
//                )
//        );
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//        System.out.print("first Name:"+userPrincipal.getFirstName()+"================");
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
////        JwtAuthenticationResponse returnValue = tokenProvider.generateToken(authentication);
//        return null;
//    }
//	
	
	@GetMapping(path = "/change")
	public String changePasswordform() {   
		return "changePassword";
	}
	
	@GetMapping(path = "/change-pwd")
	public String changePassword(@ModelAttribute("user") UserRegistrationDto changePwdDetail) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());
        
        Boolean match = passwordEncoder.matches(changePwdDetail.getOldPassword(), user.getPassword());
		if(match) {
			user.setPassword(passwordEncoder.encode(changePwdDetail.getNewPassword()));
			userRepository.save(user);
			return "redirect:/?pwdChangedSuccess";
		}
		else {
		return "redirect:/registration/change?pwdNotMatch";
		}
	}

}
