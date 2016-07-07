package com.sharedmeals.security.social;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

import com.sharedmeals.security.User;
import com.sharedmeals.security.UserService;

public class SimpleSocialUserDetailsService implements SocialUserDetailsService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	
	public SimpleSocialUserDetailsService() {
		
	}
	
	@Override
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
		UserDetails user = userService.loadUserByUsername(userId);
		if (user == null) {
			throw new UsernameNotFoundException("User " + userId + " not found");
		}
		List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		return new SocialUser(userId, user.getPassword(), user.isEnabled(), true, true, true, auth);
	}

	public User createUser(String username, String email, String facebookId, String displayName) {
		User user = new User();
		
		user.setUsername(username);
		user.setPassword(UUID.randomUUID().toString());
		user.setEmail(email);
		user.setDisplayName(displayName);
		user.setEnabled(true);
		
		log.debug("createUser(" + email + ", " + facebookId + ", displayName)");
		
		return userService.createUser(user);
	}
}
