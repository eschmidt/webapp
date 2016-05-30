package com.sharedmeals.hello;

import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {
	@RequestMapping("/hello")
	public String index(Map<String, Object> model) {
		model.put("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		
		return "hello";
	}
}
