package com.example.unitalk.services;
import com.example.unitalk.models.Comment;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommentService {
    private static int commentCounter = 0;
    private Map<Integer, Comment> comments = new HashMap<>();

    // Find comment by ID
    public Comment findById(int commentId) {
        return comments.get(commentId);
    }

    // Remove comment
    public void removeById(int commentId) {
        comments.remove(commentId);
    }

    public void createComment(User user, String text, Post post) {
        Comment newComment = new Comment(user, text, commentCounter);
        commentCounter++;
        user.addComment(newComment);
        post.addComment(newComment);
        comments.put(newComment.getId(), newComment);
    }

    public void deleteComment(User user, Comment comment, Post post) {
        user.removeComment(comment);
        post.removeComment(comment);
        comments.remove(comment.getId());
    }
}

