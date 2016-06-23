package com.sharedmeals.security;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class UserForm {
	@Email(message="{security.validation.email}")
	@NotEmpty(message="{security.validation.required}")
	private String email;
	
	@NotEmpty(message="{security.validation.required}")
	private String password;
	
	@NotEmpty(message="{security.validation.required}")
	private String passwordConf;

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPasswordConf() {
		return passwordConf;
	}

	public void setPasswordConf(String passwordConf) {
		this.passwordConf = passwordConf;
	}
}
