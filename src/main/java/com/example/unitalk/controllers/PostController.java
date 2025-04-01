package com.example.unitalk.controllers;

import com.example.unitalk.exceptions.UserNotEnrolledException;
import com.example.unitalk.repository.SubjectRepository;
import com.example.unitalk.services.PostService;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/subjects/{id}")
public class PostController {
    @Autowired
    private UserService users;
    @Autowired
    private SubjectService subjects;
    @Autowired
    private PostService posts;

    @GetMapping
    public String showPosts(Model model, @PathVariable Long id) {
        Optional<Subject> optionalSubject = subjects.findById(id);
        if (optionalSubject.isEmpty()) {
            throw new RuntimeException("Subject not found");
        }
        Subject subject = optionalSubject.get();
        List<Post> posts = subject.getPosts();
        User user = users.getUser();
        model.addAttribute("posts", posts);
        model.addAttribute("subject", subject);
        model.addAttribute("user", user);
        return "subjectPosts";
    }

    @PostMapping("/create-post")
    public String createPost(@PathVariable Long id, @RequestParam("name") String name, @RequestParam("description") String description, Model model) {
        Optional<Subject> optionalSubject = subjects.findById(id);
        if (optionalSubject.isEmpty()) {
            throw new RuntimeException("Subject not found");
        }
        Subject subject = optionalSubject.get();
        User user = users.getUser();
        if (user.getSubjects().contains(subject)) {
            posts.createPost(user, name, subject, description);
        } else {
            throw new UserNotEnrolledException("You are not enrolled in this subject.");
        }
        return "redirect:/subjects/{id}";

    }

    @PostMapping("/delete-post")
    public String deletePost(@PathVariable Long id, @RequestParam("idPost") Long idPost) {
        User user = users.getUser();
        Optional<Subject> optionalSubject = subjects.findById(id);
        if (optionalSubject.isEmpty()) {
            throw new RuntimeException("Subject not found");
        }
        Subject subject = optionalSubject.get();
        Optional<Post> optionalPost = posts.findById(idPost);
        if (optionalPost.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        Post post = optionalPost.get();
        if (!user.getSubjects().contains(subject)) {
            throw new UserNotEnrolledException("You are not enrolled in this subject.");
        }
        posts.deletePost(user, subject, post);
        post.getComments().clear();
        user.deletePostComments(idPost);
        return "redirect:/subjects/{id}";
    }
}
