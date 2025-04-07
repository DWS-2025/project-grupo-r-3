package com.example.unitalk.services;

import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.mappers.UserMapper;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.SubjectRepository;
import com.example.unitalk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private UserMapper userMapper;
    private SubjectRepository subjectRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, SubjectRepository subjectRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.subjectRepository = subjectRepository;
    }

    @PostConstruct
    public void initializeDefaultUser() {
        Optional<User> existingUser = userRepository.findByUsername("defaultUser");

        if (existingUser.isEmpty()) {
            User user = new User("defaultUser", "usuario@example.com");
            userRepository.save(user);
        }
    }

    public UserDTO getUser() {
        Optional<User> user = userRepository.findByUsername("defaultUser");
        if(user.isPresent()) {
            User user1 = user.get();
            return userMapper.toDTO(user1);
        }
        else {
            throw new RuntimeException("User default not found");
        }
    }

    public void setUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        userRepository.save(user);
    }
    public boolean isUserEnrolledInSubject(UserDTO userDTO, Long subjectId) {
        User user = userMapper.toEntity(userDTO);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        return user.getSubjects().contains(subject);
    }
}