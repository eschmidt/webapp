package com.sharedmeals;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {
	@Autowired
	private PersonRepository personRepository;
	
	@RequestMapping("/")
	public String index(Map<String, Object> model) {
		Person person = personRepository.findOne(1L);
		
		model.put("user", person.getName());
		
		return "hello";
	}
}
