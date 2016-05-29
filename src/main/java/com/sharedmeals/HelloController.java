package com.sharedmeals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@Autowired
	private PersonRepository personRepository;
	
	@RequestMapping("/")
	public String index() {
		Person person = personRepository.findOne(1L);
		
		return "Hello, " + person.getName() + "!";
	}
}
