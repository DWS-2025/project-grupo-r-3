package com.example.unitalk.services;

import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.DTOS.UserRestDTO;
import com.example.unitalk.mappers.UserMapper;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.SubjectRepository;
import com.example.unitalk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    @Autowired
    public RepositoryUserDetailsService userDetailService;

    private final UserRepository userRepository;
    private UserMapper userMapper;
    private SubjectRepository subjectRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, SubjectRepository subjectRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.subjectRepository = subjectRepository;
    }

    public List<UserDTO> getAllUsers(){
        List<User> users= userRepository.findAll();
        return userMapper.toDTO(users);
    }

    public List<UserRestDTO> getAllUsersRest(){
        List<User> users= userRepository.findAll();
        return userMapper.toRestDTO(userMapper.toDTO(users));
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
        user.setUsername(Jsoup.clean(user.getUsername(), Safelist.basic()));
        user.setEmail(Jsoup.clean(user.getEmail(), Safelist.basic()));
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
        String originalUsername = user.getUsername();
        user.setUsername(Jsoup.clean(userDTO.username(), Safelist.basic()));
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!userDTO.email().matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format: " + userDTO.email());
        }
        user.setEmail(Jsoup.clean(userDTO.email(), Safelist.basic()));
        userRepository.save(user);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !userDTO.username().equals(originalUsername)) {
            UserDetails newUserDetails = userDetailService.loadUserByUsername(userDTO.username());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    newUserDetails,
                    auth.getCredentials(),
                    auth.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        return userMapper.toDTO(user);
    }
    public UserDTO newUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(Jsoup.clean(username, Safelist.basic()));
        user.setEmail(Jsoup.clean(email, Safelist.basic()));
        user.setPassword(encodedPassword);
        user.setRoles(List.of("USER"));
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

}