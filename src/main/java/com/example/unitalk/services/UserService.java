package com.example.unitalk.services;

import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.DTOS.UserRestDTO;
import com.example.unitalk.mappers.UserMapper;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.SubjectRepository;
import com.example.unitalk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
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
    public UserDTO getUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) {
            return userMapper.toDTO(user.get());
        }
        else {
            throw new RuntimeException("User not found");
        }
    }
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
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
    public UserRestDTO getUserRestDTO() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = getUser(username);
        return userMapper.toRestDTO(userDTO);
    }
    public UserDTO modifyUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        userRepository.save(user);
        return userDTO;
    } 
}