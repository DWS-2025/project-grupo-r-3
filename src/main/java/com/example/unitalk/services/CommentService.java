package com.example.unitalk.services;
import com.example.unitalk.models.Comment;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private static long commentCounter = 0;
    public void createComment(User user, String title, String text, Post post){
        Comment newComment = new Comment(user, title,text,commentCounter);
        commentCounter++;
        user.addComment(newComment);
        post.addComment(newComment);
    }
    public void deleteComment(User user, Comment comment, Post post){
        user.removeComment(comment);
        post.removeComment(comment);
    }
    public void deleteCommentById(int commentId, User user, Post post) {
        Comment commentToDelete = null;
        for (Comment comment : user.getComments()) {
            if (comment.getId() == commentId) {
                commentToDelete= comment;
                break;
            }
        }
        if (commentToDelete != null) {
            user.getComments().remove(commentToDelete);
            post.getComments().remove(commentToDelete);
        } else {

            //TODO:CAMBIAR ESTO!!! MANEJAR CON EXCEPCIONES
            System.out.println("No se encontró el comment con id: " + commentId);
        }
    }
}
