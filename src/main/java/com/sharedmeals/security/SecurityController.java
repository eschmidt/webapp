package com.sharedmeals.security;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {
	
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private Validator passwordValidator;
	
	@Autowired
	private UserService userService;
	
	@InitBinder("userForm")
	private void initBinder(WebDataBinder binder) {
		binder.addValidators(passwordValidator);
	}
	
	@RequestMapping("/login")
	public String login(Map<String, Object> model) {
		return "login";
	}
	
	@RequestMapping("/user/register")
	public String createForm(Model model, UserForm user) {
		model.addAttribute("user", user);
		
		return "userRegisterForm";
	}
	
	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	public String create(Model model, @Validated UserForm user, BindingResult result) {
		
		if (result.hasErrors()) {
			return createForm(model, user);
		}
		
		try {
			// create user
			model.addAttribute("user", userService.createUser(user.getEmail(), user.getPassword()));
		} catch (DuplicateEmailException e) {
			model.addAttribute("error", e.getMessage());
			return createForm(model, user);
		}
		
		return "userRegisterSuccess";
	}
	
	@RequestMapping(value = "/user/verify")
	public String validate(Model model, @RequestParam("token") String token, HttpServletRequest request, HttpServletResponse response) {
		User user = userService.verifyUserEmail(token);
		
		if (user != null) {
			request.getSession();	// generate a session if one doesn't exist
				
			// automatically authenticate the user
			Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null, null);
			SecurityContextHolder.getContext().setAuthentication(auth);
			
			return "userVerificationSuccess";
		}
		
		return "userVerificationFailure";
	}
	
	@RequestMapping(value = "/user/whoami")
	public String whoami(Model model, Principal principal) {
		model.addAttribute("email", principal.getName());
		
		return "whoami";
	}
}
