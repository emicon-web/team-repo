package com.bezkoder.springjwt.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bezkoder.springjwt.models.Token;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.TokenRepository;
import com.bezkoder.springjwt.utils.Utils;

@Service
@Transactional
public class TokenServiceImpl implements ITokenService {

	@Value("${token.expire.resetTime}")
	private Integer tokenExpiryForResetPwd;

	@Value("${token.expire.generateTime}")
	private Integer tokenExpiryForGeneratePwd;

	@Autowired
	private TokenRepository tokenRepository;

	@Override
	public Token createResetPasswordTokenForUser(User user, final String tokenId, final Integer passcode) {
		Token token = new Token(user, tokenId, passcode, tokenExpiryForResetPwd);
		return tokenRepository.save(token);
	}

	@Override
	public Token createGeneratePasswordTokenForUser(User user, final String tokenId, final Integer passcode) {
		Token token = new Token(user, tokenId, passcode, tokenExpiryForGeneratePwd);
		return tokenRepository.save(token);
	}

	@Override
	public String validatePasswordResetToken(String token, Integer passcode) {
		final Token passToken = tokenRepository.findByTokenAndPasscode(token, passcode);
		return !Utils.isTokenFound(passToken) ? "invalidToken"
				: Utils.isTokenExpired(passToken) ? "expired" : null;
	}

}
