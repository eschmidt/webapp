package com.sharedmeals.security;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "app_user")
public class User {
	@Id
	@SequenceGenerator(name = "app_user_seq", sequenceName = "app_user_id_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_seq")
	private long id;
	
	private String username;
	
	@Column(unique=true)
	private String email;
	
	private String password;
	
	private String displayName;
	
	private Boolean enabled;
	
	@SuppressWarnings("unused")
	private LocalDateTime dateCreated;
	
	@SuppressWarnings("unused")
	private LocalDateTime lastUpdated;

	public User() {
		this.enabled = false;
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
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
