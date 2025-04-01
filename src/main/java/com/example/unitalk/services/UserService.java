package com.example.unitalk.services;

import com.example.unitalk.models.User;
import com.example.unitalk.repository.UserRepository; // Asegúrate de crear este repositorio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@Service
public class UserService {

    private User user;
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initializeDefaultUser() {
        // Searches if there is already a user with name "defaultUser"
        Optional<User> existingUser = userRepository.findByUsername("defaultUser");
        if (existingUser.isPresent()) {
            this.user = existingUser.get();
        } else {
            this.user = new User("defaultUser", "usuario@example.com");
            userRepository.save(user);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        userRepository.save(user);
    }
}