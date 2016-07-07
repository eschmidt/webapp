package com.sharedmeals.security.social;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.security.SocialUserDetailsService;

public class ConnectionSignUpService implements ConnectionSignUp {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private SimpleSocialUserDetailsService simpleSocialUserDetailsService;
	
	public ConnectionSignUpService(SocialUserDetailsService simpleSocialUserDetailsService) {
		this.simpleSocialUserDetailsService = (SimpleSocialUserDetailsService) simpleSocialUserDetailsService;
	}
	
	@Override
	public String execute(Connection<?> connection) {
		UserProfile profile = connection.fetchUserProfile();
		
		String userId = UUID.randomUUID().toString();
		
		log.debug("auto signup new user! userId: " + userId + " email: " + profile.getEmail() + " displayName: " + profile.getName());
		
		simpleSocialUserDetailsService.createUser(userId, profile.getEmail(), profile.getUsername(), profile.getName());
		
		return userId;
	}

}
