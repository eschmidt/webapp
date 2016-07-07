package com.sharedmeals.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.sharedmeals.WebApplication;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(WebApplication.class)
public class UserServiceTest {
	
	private final static String EMAIL = "test@user.com";
	
	private final static String PASSWORD = "test";
	
	private JavaMailSender mailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	private UserRepository userRepository;
	
	private UserService userService;
	
	private VerificationTokenRepository verificationTokenRepository;
	
	@Before
	public void setUp() {
		mailSender = mock(JavaMailSender.class);
		userRepository = mock(UserRepository.class);
		verificationTokenRepository = mock(VerificationTokenRepository.class);
		
		userService = new UserService(mailSender, templateEngine, userRepository, verificationTokenRepository);
	}
	
	@Test
	public void testCreateUser() throws DuplicateEmailException {
		// test case setup
		when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));
		doNothing().when(mailSender).send((MimeMessage) anyObject());
		
		User user = userService.createUser(EMAIL, PASSWORD);
		
		assertNotNull(user);
		assertFalse(user.isEnabled());
	}
	
	@Test
	public void testVerifyUserEmail() {
		// test case setup
		User mockUser = new User();
		mockUser.setEmail(EMAIL);
		mockUser.setEnabled(false);
		VerificationToken mockToken = new VerificationToken(mockUser, "token", LocalDateTime.now().plusDays(1));
		
		when(userRepository.findOneByUsername(EMAIL)).thenReturn(mockUser);
		when(verificationTokenRepository.findOneByToken(mockToken.getToken())).thenReturn(mockToken);
		
		// verify the user is not enabled before verification
		assertFalse(mockUser.isEnabled());
		
		// verify the user's email address
		assertTrue(userService.verifyUserEmail(mockToken.getToken()) != null);
		
		// the user is now enabled
		assertTrue(mockUser.isEnabled());
	}
}
