package com.example.unitalk.services;

import com.example.unitalk.models.Comment;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    private static final Path IMAGES_FOLDER = Paths.get("uploads/comments");

    // Find comment by ID
    public Optional<Comment> findById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public void createComment(User user, String text, Post post, MultipartFile image) throws IOException {
        String imagePath = null;
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }
        byte[] imageData = null;
        String imageName = null;

        if (image != null && !image.isEmpty()) {
            imageData = image.getBytes();
            imageName = image.getOriginalFilename();
        }
        Comment newComment = new Comment(user, text, post, imageData, imageName);
        user.addComment(newComment);
        post.addComment(newComment);
        commentRepository.save(newComment);
    }

    public void deleteComment(User user, Comment comment, Post post) {
        user.removeComment(comment);
        post.removeComment(comment);
        commentRepository.delete(comment);
    }
}

