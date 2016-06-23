package com.sharedmeals.security;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.transaction.Transactional;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.sharedmeals.WebApplication;
import com.sharedmeals.selenium.DriverBase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@Transactional
@WebIntegrationTest("server.port:9000")
public class SecurityIntegrationTest extends DriverBase {
	
	private final static String TEST_URL = "http://localhost:9000";
	
	private final static String BAD_EMAIL = "bad@test.com";
	
	private final static String GOOD_EMAIL = "good@test.com";
	
	private final static String PASSWORD = "test";
	
	private final static String TOKEN_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
	
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static GreenMail smtpServer;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;
	
	@BeforeClass
	public static void initialize() {
		smtpServer = new GreenMail(ServerSetupTest.SMTP);
		smtpServer.start();
	}
	
	@AfterClass
	public static void cleanUp() {
		smtpServer.stop();
	}
	
    @Test
    public void loginFailureForNonexistentUser() throws Exception {
        WebDriver driver = getDriver();

        // try to login using an email/password that doesn't exist
        driver.get(TEST_URL + "/login");
        driver.findElement(By.id("inputEmail")).sendKeys(BAD_EMAIL);
        driver.findElement(By.id("inputPassword")).sendKeys(PASSWORD);
        driver.findElement(By.id("loginSubmit")).click();
        
        (new WebDriverWait(driver, 10)).until(
        	ExpectedConditions.textToBePresentInElementLocated(By.id("error"), "Invalid Email or Password")
        );
    }
    
    @Test
    public void loginFailureWithoutEmailVerification() throws Exception {
    	WebDriver driver = getDriver();
    	
    	// register a new user, but do not follow verification instructions
    	// (don't click the verify link that was sent via email)
    	driver.get(TEST_URL + "/user/register");
		
    	// register the new user
    	driver.findElement(By.id("inputEmail")).sendKeys(BAD_EMAIL);
        driver.findElement(By.id("inputPassword")).sendKeys(PASSWORD);
        driver.findElement(By.id("inputPasswordConf")).sendKeys(PASSWORD);
        driver.findElement(By.id("registerSubmit")).click();
		
        // try to login with new user
        driver.get(TEST_URL + "/user/whoami");
        driver.findElement(By.id("inputEmail")).sendKeys(BAD_EMAIL);
        driver.findElement(By.id("inputPassword")).sendKeys(PASSWORD);
        driver.findElement(By.id("loginSubmit")).click();
        
        (new WebDriverWait(driver, 10)).until(
        	ExpectedConditions.textToBePresentInElementLocated(By.id("error"), "Invalid Email or Password")
        );
    }
    
    @Test
    public void loginSuccessAfterEmailVerification() throws Exception {
    	WebDriver driver = getDriver();
    	
    	// register a new user
    	driver.get(TEST_URL + "/user/register");
    	driver.findElement(By.id("inputEmail")).sendKeys(GOOD_EMAIL);
        driver.findElement(By.id("inputPassword")).sendKeys(PASSWORD);
        driver.findElement(By.id("inputPasswordConf")).sendKeys(PASSWORD);
        driver.findElement(By.id("registerSubmit")).click();
        
        // verify an email was sent with a verification code
        assertTrue(smtpServer.waitForIncomingEmail(5000, 1));
        Message[] messages = smtpServer.getReceivedMessages();
        assertEquals(1, messages.length);
        
        // grab the token from the email body
        String body = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
        Matcher m = Pattern.compile("token=(" + TOKEN_REGEX + ")").matcher(body);
        String tokenFromEmail = (m.find()) ? m.group(1) : null;
        
        // simulate the user clicking the verification link from their email
        User user = userRepository.findOneByEmail(GOOD_EMAIL);
        VerificationToken token = verificationTokenRepository.findOneByUser(user);
        assertEquals(token.getToken(), tokenFromEmail);
        driver.get(TEST_URL + "/user/verify?token=" + token.getToken());
		
        // access a page that requires authentication to verify that we are now logged in successfully
        driver.get(TEST_URL + "/user/whoami");
        
        (new WebDriverWait(driver, 10)).until(
        	ExpectedConditions.textToBePresentInElementLocated(By.id("user"), GOOD_EMAIL)
        );
    }
}