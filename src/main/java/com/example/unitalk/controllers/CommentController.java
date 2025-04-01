package com.example.unitalk.controllers;


import com.example.unitalk.models.*;
import com.example.unitalk.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
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
    public String showPostComments(@PathVariable("id1") Long idSubject, @PathVariable("id2") Long idPost, Model model) {
        User user = users.getUser();
        Optional<Subject> optionalSubject = subjects.findById(idSubject);
        if (optionalSubject.isEmpty()) {
            throw new RuntimeException("Subject not found");
        }
        Subject subject = optionalSubject.get();
        Optional<Post> optionalPost = posts.findById(idPost);
        if (optionalPost.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        Post post = optionalPost.get();
        if (user.getSubjects().contains(subject)) {
            model.addAttribute("comments", post.getComments());
            model.addAttribute("post", post);
            model.addAttribute("subject", subject);
        } else {
            model.addAttribute("error", "You have not applied to this subject");
        }
        return "post";
    }

    @PostMapping("/comment")
    public String addComment(@PathVariable("id1") Long idSubject, @PathVariable("id2") Long idPost, @RequestParam("commentText") String commentText, @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        User user = users.getUser();
        Optional<Post> optionalPost = posts.findById(idPost);
        if (optionalPost.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        Post post = optionalPost.get();
        comments.createComment(user, commentText, post, image);
        return "redirect:/subjects/{id1}/posts/{id2}";
    }

    @PostMapping("/edit-comment")
    public String editComment(@PathVariable("id1") Long idSubject, @PathVariable("id2") Long idPost, @RequestParam("commentId") Long commentId, @RequestParam("commentText") String commentText) {
        Optional<Comment> optionalComment = comments.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }
        Comment comment = optionalComment.get();
        comment.setText(commentText);
        return "redirect:/subjects/{id1}/posts/{id2}";
    }

    @PostMapping("/delete-comment")
    public String deleteComment(@PathVariable("id1") Long idSubject, @PathVariable("id2") Long idPost, @RequestParam("commentId") Long commentId) {
        User user = users.getUser();
        Optional<Post> optionalPost = posts.findById(idPost);
        if (optionalPost.isEmpty()) {
            throw new RuntimeException("Post not found");
        }
        Post post = optionalPost.get();
        Optional<Comment> optionalComment = comments.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }
        Comment comment = optionalComment.get();
        comments.deleteComment(user, comment, post);
        return "redirect:/subjects/{id1}/posts/{id2}";
    }

    @GetMapping("/comment-image/{id}")
    public ResponseEntity<byte[]> getCommentImage(@PathVariable Long id) {
        Optional<Comment> optionalComment = comments.findById(id);
        if (optionalComment.isPresent() && optionalComment.get().getImageData() != null) {
            return ResponseEntity.ok().body(optionalComment.get().getImageData());
        }
        return ResponseEntity.notFound().build();
    }


}
