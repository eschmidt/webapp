package com.sharedmeals.security;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;

@Entity
public class VerificationToken {
	@Id
	@SequenceGenerator(name = "verification_token_seq", sequenceName = "verification_token_id_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_token_seq")
	private long id;
	
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, unique=true)
    private User user;
    
    @Column(unique=true)
	private String token;
	
	private LocalDateTime expires;
	
	@SuppressWarnings("unused")
	private LocalDateTime dateCreated;
	
	@SuppressWarnings("unused")
	private LocalDateTime lastUpdated;
	
	public VerificationToken() {
		// default constructor
	}
	
	public VerificationToken(final User user, final String token, final LocalDateTime expires) {
		this.user = user;
		this.token = token;
		this.expires = expires;
	}

	@PrePersist
	protected void onCreate() {
		this.dateCreated = LocalDateTime.now();
		this.lastUpdated = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.lastUpdated = LocalDateTime.now();
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getExpires() {
		return expires;
	}

	public void setExpires(LocalDateTime expires) {
		this.expires = expires;
	}
}
