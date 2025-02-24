package com.example.unitalk.services;
import com.example.unitalk.models.Comment;
import com.example.unitalk.models.User;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private static long commentCounter = 0;
    public void createComment(User user, String title, String text){
        Comment newComment = new Comment(user, title,text,commentCounter);
        commentCounter++;
        user.addComment(newComment);
    }
    public void deleteComment(User user, Comment comment){

    }
}
