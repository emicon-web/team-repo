package com.bezkoder.springjwt.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());

        if (request.getAttribute(ExpiredJwtException.class.getSimpleName()) != null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Authentication token is expired");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
        }

    }

}
