package com.example.unitalk.services;

import com.example.unitalk.models.User;
import com.example.unitalk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initializeDefaultUser() {
        Optional<User> existingUser = userRepository.findByUsername("defaultUser");

        if (existingUser.isEmpty()) {
            User user = new User("defaultUser", "usuario@example.com");
            userRepository.save(user);
        }
    }

    public User getUser() {
        Optional<User> user = userRepository.findByUsername("defaultUser");
        return user.orElseThrow(() -> new RuntimeException("Usuario default no encontrado"));
    }

    public void setUser(User user) {
        userRepository.save(user);
    }
}