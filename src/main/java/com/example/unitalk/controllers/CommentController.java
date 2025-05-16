package com.example.unitalk.controllers;

import com.example.unitalk.DTOS.*;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/subjects/{id1}/posts/{id2}")
public class CommentController {

    @Autowired
    private SubjectService subjects;
    @Autowired
    private PostService posts;
    @Autowired
    private UserService users;
    @Autowired
    private CommentService comments;

    @GetMapping
    public String showPostComments(
            @PathVariable("id1") Long idSubject,
            @PathVariable("id2") Long idPost,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = users.getUser(username);
        Optional<SubjectDTO> optionalSubject = subjects.findById(idSubject);
        if (optionalSubject.isEmpty()) {
            throw new RuntimeException("Subject not found");
        }

        SubjectDTO subjectDTO = optionalSubject.get();
        Optional<PostDTO> optionalPost = posts.findById(idPost);
        if (optionalPost.isEmpty()) {
            throw new RuntimeException("Post not found");
        }

        PostDTO postDTO = optionalPost.get();

        if (userDTO != null && users.isUserEnrolledInSubject(userDTO, idSubject)) {
            model.addAttribute("post", postDTO);
            model.addAttribute("subject", subjectDTO);
        } else {
            model.addAttribute("message", "You have not applied to this subject");
            return "redirect:/error";
        }

        return "post";
    }

    @PostMapping("/comment")
    public String addComment(@PathVariable("id1") Long idSubject, @PathVariable("id2") Long idPost, @RequestParam("commentText") String commentText, @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = users.getUser(username);
        Optional<PostDTO> optionalPostDTO = posts.findById(idPost);
        if (optionalPostDTO.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        PostDTO postDTO = optionalPostDTO.get();

        CommentInputDTO commentInputDTO = new CommentInputDTO(commentText, image != null ? image.getBytes() : null, image != null ? image.getOriginalFilename() : null);
        comments.createComment(userDTO, commentInputDTO, postDTO);
        return "redirect:/subjects/{id1}/posts/{id2}";
    }

    @PostMapping("/edit-comment")
    public String editComment(@PathVariable("id1") Long idSubject, @PathVariable("id2") Long idPost, @RequestParam("commentId") Long commentId, @RequestParam("commentText") String commentText) {
        CommentInputDTO commentInputDTO = new CommentInputDTO(commentText, null, null); // No se permite editar la imagen
        comments.editComment(commentId, commentInputDTO);
        return "redirect:/subjects/{id1}/posts/{id2}";
    }

    @PostMapping("/delete-comment")
    public String deleteComment(@PathVariable("id1") Long idSubject, @PathVariable("id2") Long idPost, @RequestParam("commentId") Long commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = users.getUser(username);
        Optional<PostDTO> optionalPostDTO = posts.findById(idPost);
        if (optionalPostDTO.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        PostDTO postDTO = optionalPostDTO.get();
        comments.deleteComment(userDTO, commentId, postDTO);
        return "redirect:/subjects/{id1}/posts/{id2}";
    }
}