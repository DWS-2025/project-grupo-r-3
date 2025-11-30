package com.example.unitalk.security;

import com.example.unitalk.models.User;
import com.example.unitalk.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if user has 2FA enabled
            if (user.isTwoFactorEnabled()) {
                // Store authentication in session for 2FA verification
                HttpSession session = request.getSession();
                session.setAttribute("2FA_USERNAME", username);
                session.setAttribute("2FA_AUTHENTICATED", false);
                session.setAttribute("2FA_AUTHENTICATION", authentication);

                // Clear authentication from SecurityContext - user is NOT fully authenticated yet
                SecurityContextHolder.clearContext();

                // Redirect to 2FA verification page
                response.sendRedirect("/2fa/verify");
                return;
            }
        }

        // If 2FA is not enabled, proceed with normal login
        response.sendRedirect("/");
    }
}
