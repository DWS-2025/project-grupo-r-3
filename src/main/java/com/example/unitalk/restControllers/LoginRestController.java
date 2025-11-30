package com.example.unitalk.restControllers;

import com.example.unitalk.security.jwt.AuthResponse;
import com.example.unitalk.security.jwt.LoginRequest;
import com.example.unitalk.security.jwt.UserLoginService;
import com.example.unitalk.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginRestController {
    @Autowired
    private UserLoginService userService;
    @Autowired
    private UserService users;

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

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody Map<String, Object> signupData) {
        try {

            String username = (String) signupData.get("username");
            String email = (String) signupData.get("email");
            String password = (String) signupData.get("password");

            if (username == null || username.isEmpty() || email == null || email.isEmpty() || password == null
                    || password.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
                errorResponse.put("message", "Username, email, and password are required");
                errorResponse.put("timestamp", java.time.LocalDateTime.now());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            users.newUser(username, email, password);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("status", HttpStatus.CREATED.value());
            successResponse.put("message", "User created successfully! Please log in.");
            successResponse.put("timestamp", java.time.LocalDateTime.now());
            return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.put("message", "An error occurred while creating the user");
            errorResponse.put("timestamp", java.time.LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}