package com.example.unitalk.restControllers;

import com.example.unitalk.security.jwt.AuthResponse;
import com.example.unitalk.security.jwt.LoginRequest;
import com.example.unitalk.security.jwt.UserLoginService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginRestController {
    @Autowired
    private UserLoginService userService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        return userService.login(response, loginRequest);
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        return userService.refresh(response, refreshToken);
    }
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
        return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS, userService.logout(response)));
    }
}