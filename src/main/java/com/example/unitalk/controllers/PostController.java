package com.example.unitalk.controllers;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;

import java.util.List;

@Controller
@RequestMapping("/subjects/{id}")
public class PostController {
    @Autowired
    private UserService users;
    @Autowired
    private SubjectService subjects;

    @GetMapping
    public String showPosts(Model model, @PathVariable int id){
        Subject subject = subjects.findById(id);
        List<Post> posts = subject.getPosts();
        User user = users.getUser();
        model.addAttribute("posts",posts);
        model.addAttribute("subject",subject);
        model.addAttribute("user",user);
        return "subjectPosts";
    }
    @PostMapping
    public String createPost(@PathVariable int id, @RequestParam("name") String name, @RequestParam("description") String description){
        Subject subject = subjects.findById(id);
        User user = users.getUser();
        Post post = new Post(name,description,subject,user);
        user.addPost(post);
        subject.addPost(post);
        return "redirect:/subjects/{id}";
    }
}
