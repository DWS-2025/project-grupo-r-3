package com.example.unitalk.restControllers;

import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.DTOS.UserRestDTO;
import com.example.unitalk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminRestController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserRestDTO>> getAllUsers() {
        List<UserRestDTO> users = userService.getAllUsersRest();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/delete-user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username) {
        try {
            userService.deleteUser(username);
            return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
