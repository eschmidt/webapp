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
	
	@Column(unique=true)
	private String email;
	
	private String password;
	
	private Boolean enabled;
	
	@SuppressWarnings("unused")
	private LocalDateTime dateCreated;
	
	@SuppressWarnings("unused")
	private LocalDateTime lastUpdated;

	public User() {
		this.enabled = false;
	}
	
	public User(String email) {
		this.email = email;
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

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
}
