package com.example.unitalk.security.jwt;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class UnauthorizedHandlerJwt implements AuthenticationEntryPoint, AccessDeniedHandler {

  private static final Logger logger = LoggerFactory.getLogger(UnauthorizedHandlerJwt.class);

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
          throws IOException {
    logger.info("Unauthorized error: {}", authException.getMessage());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.getWriter().write(String.format(
            "{\"status\": %d, \"error\": \"Unauthorized\", \"message\": \"%s\", \"path\": \"%s\"}",
            HttpStatus.UNAUTHORIZED.value(), authException.getMessage(), request.getServletPath()));
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
          throws IOException {
    logger.info("Access denied: {}", accessDeniedException.getMessage());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.getWriter().write(String.format(
            "{\"status\": %d, \"error\": \"Forbidden\", \"message\": \"%s\", \"path\": \"%s\"}",
            HttpStatus.FORBIDDEN.value(), accessDeniedException.getMessage(), request.getServletPath()));
  }
}
