package com.bezkoder.springjwt.security.jwt;

import com.bezkoder.springjwt.errors.GenericCustomException;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${bezkoder.app.jwtSecret}")
	private String jwtSecret;

	@Value("${bezkoder.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	private static String JWT_SECRET;

	@Value("${bezkoder.app.jwtSecret}")
	public void setJwtSecret(String secret) {
		JwtUtils.JWT_SECRET = secret;
	}

	@Autowired
	private UserRepository userRepository;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs*60000))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public static String getUserNameFromJwt(String token) {
		return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody().getSubject();
	}

	public Date getIssuedAtFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getIssuedAt();
	}

	public Date getExpiratioDateFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
	}

	public boolean validateJwtToken(String authToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);

			Optional<User> optionalUser = userRepository.findByUserName(getUserNameFromJwtToken(authToken));
			// once user logout, same token can not be reused even if token expiry time in future
			if (optionalUser.isPresent()) {
				User user = optionalUser.get();
				if (user.getUserLastLogoutTime() != null
						&& user.getUserLastLogoutTime().after(getIssuedAtFromJwtToken(authToken))) {
					throw new Exception("User has already sign out or Invalid JWT token");
				}
			}
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
			new GenericCustomException("Invalid JWT signature: {}"+ e.getMessage(), new Date());

		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
			new GenericCustomException("Invalid JWT token: {}"+ e.getMessage(), new Date());

		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
			request.setAttribute(ExpiredJwtException.class.getSimpleName(), e);
			new GenericCustomException("JWT token is expired: {}"+ e.getMessage(), new Date());

		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
			new GenericCustomException("JWT token is unsupported: {}"+ e.getMessage(), new Date());

		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
			new GenericCustomException("JWT token is empty: {}"+ e.getMessage(), new Date());

		} catch (Exception e) {
			logger.error("User has already sign out or Invalid JWT token");
			new GenericCustomException("User has already sign out or Invalid JWT token");

		}

		return false;
	}

	public static String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7);
		}

		return null;
	}

}
