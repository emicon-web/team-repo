package com.bezkoder.springjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bezkoder.springjwt.models.Token;
import com.bezkoder.springjwt.models.User;

public interface TokenRepository extends JpaRepository<Token, Long> {

	Token findByToken(String token);

	Token findByUser(User user);
	
	Token findByTokenAndPasscode(String token, Integer passcode);
}
