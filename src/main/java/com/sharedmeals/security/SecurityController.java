package com.sharedmeals.security;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecurityController {
	@RequestMapping("/login")
	public String login(Map<String, Object> model) {
		return "login";
	}
}
