package com.example.unitalk.services;

import com.example.unitalk.DTOS.SubjectInputDTO;
import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.models.Comment;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.CommentRepository;
import com.example.unitalk.repository.PostRepository;
import com.example.unitalk.repository.SubjectRepository;
import com.example.unitalk.repository.UserRepository;
import com.example.unitalk.services.PostService;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository; // Asegúrate de tenerlo si usas comentarios
    private final UserService userService;
    private final SubjectService subjectService;
    private final PostService postService;

    @Autowired
    public DataInitializer(UserRepository userRepository, SubjectRepository subjectRepository,
                           PostRepository postRepository, CommentRepository commentRepository,
                           UserService userService, SubjectService subjectService, PostService postService) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.subjectService = subjectService;
        this.postService = postService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Solo inicializar si no hay datos
        if (userRepository.count() == 0 && subjectRepository.count() == 0 && postRepository.count() == 0) {
            System.out.println("Initializing sample data...");

            User user = new User("defaultUser", "usuario@example.com");
            userRepository.save(user);

            Subject subject1 = new Subject("Initialized Subject");
            Subject subject2 = new Subject("Physics");
            Subject subject3 = new Subject("Mathematics");

            subjectRepository.saveAll(List.of(subject1, subject2));

            user.addSubject(subject1);
            userRepository.save(user);
            Post post1 = null;
            Post post2 = null;
            for (int i = 1; i <= 10; i++) {
                post1 = new Post("Post " + i, "Description " + i, subject1, user);
                postRepository.save(post1);
            }

            for (int i = 1; i <= 50; i++) {
                Comment comment = new Comment("Comment " + i, user, post1);
                commentRepository.save(comment);
                post1.addComment(comment);
                postRepository.save(post1);
            }

            System.out.println("Sample data initialized successfully!");
        } else {
            System.out.println("Data already initialized, skipping...");
        }
    }
}