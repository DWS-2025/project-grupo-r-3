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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// @Component - Comentado porque la base de datos ya tiene datos
// Descomentar solo si necesitas reinicializar la base de datos desde cero
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final SubjectService subjectService;
    private final PostService postService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, SubjectRepository subjectRepository,
            PostRepository postRepository, CommentRepository commentRepository,
            UserService userService, SubjectService subjectService, PostService postService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.subjectService = subjectService;
        this.postService = postService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Solo inicializar si no hay usuarios en la base de datos
        if (userRepository.count() == 0) {
            System.out.println("Initializing sample data...");

            // Create subjects
            Subject subject1 = new Subject("Initialized Subject");
            Subject subject2 = new Subject("Physics");
            Subject subject3 = new Subject("Mathematics");

            subjectRepository.saveAll(List.of(subject1, subject2, subject3));

            // Create users with 2FA disabled by default
            User user = new User("user1", passwordEncoder.encode("user"), "user@gmail.com", "USER");
            User user2 = new User("user2", passwordEncoder.encode("user2"), "user2@gmail.com", "USER");
            User user3 = new User("admin", passwordEncoder.encode("admin"), "admin@gmail.com", "USER", "ADMIN");

            // Initialize 2FA fields for all users
            user.setTwoFactorEnabled(false);
            user2.setTwoFactorEnabled(false);
            user3.setTwoFactorEnabled(false);

            user3.setSubjects(List.of(subject1, subject2, subject3));
            userRepository.save(user);
            userRepository.save(user2);
            userRepository.save(user3);

            user.addSubject(subject1);
            userRepository.save(user);

            // Create posts
            Post post1 = null;
            Post post2 = null;
            for (int i = 1; i <= 10; i++) {
                post1 = new Post("Post " + i, "Description " + i, subject1, user);
                postRepository.save(post1);
            }

            // Create comments
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