package com.example.unitalk.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class TwoFactorAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Skip filter for public endpoints
        String path = request.getRequestURI();
        if (path.equals("/") || 
            path.startsWith("/css/") || 
            path.startsWith("/js/") || 
            path.startsWith("/images/") ||
            path.startsWith("/error") ||
            path.equals("/login") ||
            path.equals("/login/signup") ||
            path.equals("/loginerror") ||
            path.startsWith("/2fa/verify") ||
            path.startsWith("/api/auth/login") ||
            path.startsWith("/api/auth/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            // Check if user has pending 2FA verification
            String username2FA = (String) session.getAttribute("2FA_USERNAME");
            Boolean twoFactorAuthenticated = (Boolean) session.getAttribute("2FA_AUTHENTICATED");
            
            if (username2FA != null && (twoFactorAuthenticated == null || !twoFactorAuthenticated)) {
                // User has pending 2FA verification - clear authentication and redirect
                SecurityContextHolder.clearContext();
                session.invalidate();
                response.sendRedirect("/login?2fa_required=true");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

