package com.example.unitalk.controllers;
import com.example.unitalk.exceptions.UserNotEnrolledException;
import com.example.unitalk.services.PostService;
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
    @Autowired
    private PostService posts;
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

    @PostMapping("/create-post")
    public String createPost(@PathVariable int id, @RequestParam("name") String name, @RequestParam("description") String description, Model model){
        Subject subject = subjects.findById(id);
        User user = users.getUser();
        if(user.getSubjects().contains(subject)){
            posts.createPost(user, name, subject, description);
        }
        else{
            throw new UserNotEnrolledException("You are not enrolled in this subject.");        }
        return "redirect:/subjects/{id}";

    }
    @PostMapping("/delete-post")
    public String deletePost(@PathVariable int id, @RequestParam("idPost") int idPost){
        User user = users.getUser();
        Subject subject = subjects.findById(id);
        Post post = posts.findById(idPost);
        if (!user.getSubjects().contains(subject)) {
            throw new UserNotEnrolledException("You are not enrolled in this subject.");
        }
        posts.deletePost(user, subject, post);
        return "redirect:/subjects/{id}";
    }
}
