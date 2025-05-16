package com.example.unitalk.controllers;

import com.example.unitalk.DTOS.PostDTO;
import com.example.unitalk.DTOS.PostInputDTO;
import com.example.unitalk.DTOS.SubjectDTO;
import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.services.PostService;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/subjects/{id}")
public class PostController {

    @Autowired
    private UserService userService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private PostService postService;

    @GetMapping
    public String showPosts(@PathVariable Long id,
                            @RequestParam(required = false) String titleFilter,
                            @RequestParam(required = false) String descriptionFilter,
                            Model model) {
        Optional<SubjectDTO> optionalSubjectDTO = subjectService.findById(id);
        if (optionalSubjectDTO.isEmpty()) {
            throw new RuntimeException("Subject not found");
        }
        SubjectDTO subjectDTO = optionalSubjectDTO.get();
        List<PostDTO> postsDTO = postService.findByDynamicFilters(id,titleFilter, descriptionFilter);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        model.addAttribute("posts", postsDTO);
        model.addAttribute("subject", subjectDTO);
        model.addAttribute("user", userDTO);
        model.addAttribute("titleFilter", titleFilter != null ? titleFilter : "");
        model.addAttribute("descriptionFilter", descriptionFilter != null ? descriptionFilter : "");
        return "subjectPosts";
    }

    @PostMapping("/create-post")
    public String createPost(@PathVariable Long id, @ModelAttribute PostInputDTO postDTO, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        Optional<SubjectDTO> optionalSubject = subjectService.findById(id);
        if (optionalSubject.isEmpty()) {
            throw new RuntimeException("Subject not found");
        }
        SubjectDTO subjectDTO = optionalSubject.get();
        try {
            postService.createPost(userDTO, subjectDTO, postDTO);
            redirectAttributes.addFlashAttribute("status", "Post created successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("status", "Error: " + e.getMessage());
        }
        return "redirect:/subjects/{id}";
    }

    @PostMapping("/delete-post")
    public String deletePost(@PathVariable Long id, @RequestParam("idPost") Long idPost, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        Optional<SubjectDTO> optionalSubject = subjectService.findById(id);
        if (optionalSubject.isEmpty()) {
            throw new RuntimeException("Subject not found");
        }
        SubjectDTO subjectDTO = optionalSubject.get();
        try {
            postService.deletePost(userDTO, subjectDTO, idPost);
            redirectAttributes.addFlashAttribute("status", "Post deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("status", "Error: " + e.getMessage());
            return "redirect:/error";
        }
        return "redirect:/subjects/{id}";
    }
}