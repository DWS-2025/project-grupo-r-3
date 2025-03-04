package com.example.unitalk.services;

import com.example.unitalk.models.Comment;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommentService {
    private static final Path IMAGES_FOLDER = Paths.get("uploads/comments");
    private static int commentCounter = 0;
    private final Map<Integer, Comment> comments = new HashMap<>();

    // Find comment by ID
    public Comment findById(int commentId) {
        return comments.get(commentId);
    }

    public void createComment(User user, String text, Post post, MultipartFile image) throws IOException {
        String imagePath = null;
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }
        if (image != null && !image.isEmpty()) {
            Files.createDirectories(IMAGES_FOLDER);
            String imageFileName = commentCounter + "_" + image.getOriginalFilename();
            Path destinationPath = IMAGES_FOLDER.resolve(imageFileName);
            image.transferTo(destinationPath);
            imagePath = imageFileName;
        }
        Comment newComment = new Comment(user, text, post, commentCounter, imagePath);
        commentCounter++;
        comments.put(newComment.getId(), newComment);
        user.addComment(newComment);
        post.addComment(newComment);
    }

    public void deleteComment(User user, Comment comment, Post post) {
        user.removeComment(comment);
        post.removeComment(comment);
        comments.remove(comment.getId());
    }
}

