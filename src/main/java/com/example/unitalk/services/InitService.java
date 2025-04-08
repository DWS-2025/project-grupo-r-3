package com.example.unitalk.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.unitalk.DTOS.PostInputDTO;
import com.example.unitalk.DTOS.SubjectDTO;
import com.example.unitalk.DTOS.SubjectInputDTO;
import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.mappers.*;
import com.example.unitalk.models.User;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class InitService {
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private UserService userService;
    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private UserMapper userMapper;

    @PostConstruct
    @Transactional
    public void init() {
        User user = new User("defaultUser", "example@email.com");
        SubjectInputDTO s = new SubjectInputDTO(null, "Economics");
        SubjectInputDTO s1 = new SubjectInputDTO(null, "English");
        SubjectInputDTO s2 = new SubjectInputDTO(null, "Network");
        subjectService.createSubject(s);
        subjectService.createSubject(s1);
        subjectService.createSubject(s2);
        UserDTO userDTO = userMapper.toDTO(user);
        // subjectService.applySubject(userDTO, 2L); // Inscribe al usuario en "English"
        // PostInputDTO p = new PostInputDTO("Hola", "Bocata de pollo");
        // SubjectDTO subject = subjectService.findById(2L).get();
        // postService.createPost(userDTO, subject, p);
    }
}