package com.example.unitalk.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.unitalk.DTOS.SubjectDTO;
import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class InitService {

    @Autowired
    CommentService commentService;
    @Autowired
    PostService postService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    UserService userService;

    @PostConstruct
    public void init() {
        userService.initializeDefaultUser();
        UserDTO user = userService.getUser();
        SubjectDTO subjectDTO = subjectService.toDTO(new Subject(1, "Fixed-Rate Mortgage", user, null));
        subjectService.createSubject(null);
    }
    
}
