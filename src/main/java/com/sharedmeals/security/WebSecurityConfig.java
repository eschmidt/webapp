package com.sharedmeals.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private TemplateResolver servletContextTemplateResolver;
	
	@Autowired
	private UserDetailsService userService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers(
						"/",
						"/css/**",
						"/js/**",
						"/login**",
						"/user/register**",
						"/user/verify**",
						"/logout").permitAll()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll()
				.logoutSuccessUrl("/login");
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	public PasswordValidator passwordValidator() {
		PasswordValidator passwordValidator = new PasswordValidator();
		return passwordValidator;
	}
	
	/*
	@Bean
	public TemplateResolver templateResolver() {
	    TemplateResolver resolver = new ServletContextTemplateResolver();
	    //resolver.setPrefix("/WEB-INF/views/");
	    resolver.setPrefix("/templates/");
	    resolver.setSuffix(".html");
	    resolver.setCharacterEncoding("UTF-8");
	    resolver.setTemplateMode("HTML5");
	    resolver.setOrder(2);
	    return resolver;
	}
	*/

	@Bean
	public TemplateResolver emailTemplateResolver() {
	    TemplateResolver resolver = new ClassLoaderTemplateResolver();
	    resolver.setPrefix("mail/");
	    resolver.setSuffix(".html");
	    resolver.setTemplateMode("HTML5");
	    resolver.setCharacterEncoding("UTF-8");
	    resolver.setOrder(1);
	    return resolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
	    final SpringTemplateEngine engine = new SpringTemplateEngine();
	    final Set<TemplateResolver> templateResolvers = new HashSet<TemplateResolver>();
	    servletContextTemplateResolver.setOrder(2);
	    templateResolvers.add(emailTemplateResolver());
	    templateResolvers.add(servletContextTemplateResolver);
	    //templateResolvers.add(templateResolver());
	    engine.setTemplateResolvers(templateResolvers);
	    return engine;
	}
}
