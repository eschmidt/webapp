package com.sharedmeals.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class PasswordValidator extends LocalValidatorFactoryBean implements Validator {
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean supports(Class<?> paramClass) {
		return UserForm.class.equals(paramClass);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		UserForm user = (UserForm) obj;
		
		if (!user.getPassword().equals(user.getPasswordConf())) {
			errors.rejectValue("passwordConf", "security.validation.passwordMismatch");
		}
	}

}
