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
    public ResponseEntity<UserRestDTO> getCurrentUser() {
        UserRestDTO userRestDTO = userService.getUserRestDTO();
        return new ResponseEntity<>(userRestDTO, HttpStatus.OK);
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
        subjectService.unapplySubject(userDTO, subjectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}