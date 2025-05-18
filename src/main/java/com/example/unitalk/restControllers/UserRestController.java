package com.example.unitalk.restControllers;

import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.DTOS.UserRestDTO;
import com.example.unitalk.exceptions.ResourceNotFoundException;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final SubjectService subjectService;

    @Autowired
    public UserRestController(UserService userService, SubjectService subjectService) {
        this.userService = userService;
        this.subjectService = subjectService;
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        UserDTO userDTO = userService.getUser(username);

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userDTO.id());
        userData.put("username", userDTO.username());
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        userData.put("isAdmin", isAdmin);

        return ResponseEntity.ok(userData);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRestDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        if (!id.equals(userDTO.id())) {
            throw new IllegalArgumentException("ID in path does not match ID in body");
        }
        userService.setUser(userDTO);
        UserRestDTO userRestDTO = userService.getUserRestDTO();
        return new ResponseEntity<>(userRestDTO, HttpStatus.OK);
    }

    @PostMapping("/{userId}/subjects/{subjectId}") 
    public ResponseEntity<Void> applySubject(@PathVariable Long userId, @PathVariable Long subjectId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        if (!userId.equals(userDTO.id())) {
            throw new ResourceNotFoundException("User ID does not match authenticated user");
        }
        subjectService.applySubject(userDTO, subjectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    }
    
    
    @DeleteMapping("/{userId}/subjects/{subjectId}")
    public ResponseEntity<Void> unapplySubject(@PathVariable Long userId, @PathVariable Long subjectId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        if (!userId.equals(userDTO.id())) {
            throw new ResourceNotFoundException("User ID does not match authenticated user");
        }
        subjectService.unapplySubject(username, subjectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(Authentication authentication, jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) {
        try {
            if (authentication == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
                errorResponse.put("message", "User not authenticated");
                errorResponse.put("timestamp", java.time.LocalDateTime.now());
                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
            }
            String username = authentication.getName();
            UserDTO userDTO = userService.getUser(username);
            if (userDTO != null) {
                userService.deleteUser(userDTO.username());
                new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler().logout(request, response, authentication);
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("status", HttpStatus.OK.value());
                successResponse.put("message", "Your profile has been deleted");
                successResponse.put("timestamp", java.time.LocalDateTime.now());
                return new ResponseEntity<>(successResponse, HttpStatus.OK);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", HttpStatus.NOT_FOUND.value());
                errorResponse.put("message", "User not found");
                errorResponse.put("timestamp", java.time.LocalDateTime.now());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.put("message", "An error occurred while deleting the user");
            errorResponse.put("timestamp", java.time.LocalDateTime.now());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}