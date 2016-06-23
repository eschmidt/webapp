package com.sharedmeals.security;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service
public class UserService implements UserDetailsService {
	
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;
	
	public UserService() {
		// default constructor
	}

	public UserService(JavaMailSender mailSender, SpringTemplateEngine templateEngine, UserRepository userRepository,
			VerificationTokenRepository verificationTokenRepository) {
		this.mailSender = mailSender;
		this.templateEngine = templateEngine;
		this.userRepository = userRepository;
		this.verificationTokenRepository = verificationTokenRepository;
	}

	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		User user = userRepository.findOneByEmail(email);
		
		if (user == null) {
			throw new UsernameNotFoundException("Email address " + email + " not found");
		}
		
		if (!user.isEnabled()) {
			throw new UsernameNotFoundException("User with email address " + email + " not enabled");
		}
		
		String password = user.getPassword();
		
		List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		
		return new org.springframework.security.core.userdetails.User(email, password, auth);
	}

	public User createUser(final String email, final String password) throws DuplicateEmailException {
		User user = userRepository.findOneByEmail(email);
		
		if (user != null) {
			if (user.isEnabled()) {
				throw new DuplicateEmailException("User with email address " + email + " already exists");
			} else {
				// user is trying to register again?  update their verification token
				VerificationToken token = verificationTokenRepository.findOneByUser(user);
				token.setExpires(LocalDateTime.now().plusDays(1));
				verificationTokenRepository.save(token);
				sendVerificationEmail(user.getEmail(), token.getToken());
			}
		} else {
			user = new User();
			user.setEmail(email);
		}
		
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode(password));
		user.setEnabled(false);
		
		userRepository.save(user);
		
		String token = createVerificationToken(user);
		sendVerificationEmail(email, token);
		
		return user;
	}
	
	private String createVerificationToken(final User user) {
		final String token = UUID.randomUUID().toString();
		
		VerificationToken verificationToken = verificationTokenRepository.findOneByUser(user);
		
		// Create a verificationToken row if one doesn't already exist for this user
		if (verificationToken == null) {
			verificationToken = new VerificationToken();
			verificationToken.setUser(user);
		}
		
		verificationToken.setToken(token);
		verificationToken.setExpires(LocalDateTime.now().plusDays(1));
		verificationTokenRepository.save(verificationToken);
		
		return token;
	}
	
	private void sendVerificationEmail(final String email, final String token) {
		Context context = new Context();
		context.setVariable("token", token);
		
		String htmlContent = templateEngine.process("verificationToken", context);
		
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, true);
			msg.setSubject("Verification Token");
			msg.setTo(email);
			msg.setText(htmlContent, true);
			
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public User verifyUserEmail(final String userToken) {
		VerificationToken token = verificationTokenRepository.findOneByToken(userToken);
		
		if (token != null) {
			User user = userRepository.findOneByEmail(token.getUser().getEmail());
			if (user != null && token.getExpires().isAfter(LocalDateTime.now())) {
				user.setEnabled(true);
				userRepository.save(user);
				return user;
			}
		}
		
		return null;
	}
}
