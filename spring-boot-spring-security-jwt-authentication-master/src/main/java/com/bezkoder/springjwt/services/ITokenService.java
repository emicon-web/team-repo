package com.bezkoder.springjwt.services;

import com.bezkoder.springjwt.models.Token;
import com.bezkoder.springjwt.models.User;

public interface ITokenService {

	Token createResetPasswordTokenForUser(User user, final String tokenId, final Integer passcode);

	Token createGeneratePasswordTokenForUser(User user, final String tokenId, final Integer passcode);

	String validatePasswordResetToken(String token, Integer passcode);
}
