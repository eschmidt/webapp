package com.sharedmeals.security;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {
	public void deleteByUser(User user);
	
	public VerificationToken findOneByUser(User user);
	
	public VerificationToken findOneByToken(String token);
}