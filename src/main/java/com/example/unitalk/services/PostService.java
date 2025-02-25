package com.example.unitalk.services;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;

public class PostService {
    public void createPost(User user, String title, Subject subject, String description){
        Post newPost = new Post(title,description,subject,user);
        user.addPost(newPost);
        subject.addPost(newPost);
    }
    public void deletePost(User user, Subject subject, Post post){
        user.removePost(post);
        subject.removePost(post);
    }
    public void deletePostById(int postId, User user, Subject subject) {
        Post postToDelete = null;
        for (Post post : user.getPosts()) {
            if (post.getId() == postId) {
                postToDelete = post;
                break;
            }
        }
        if (postToDelete != null) {
            user.getPosts().remove(postToDelete);
            subject.getPosts().remove(postToDelete);
        } else {

            //TODO:CAMBIAR ESTO!!! MANEJAR CON EXCEPCIONES
            System.out.println("No se encontró el post con id: " + postId);
        }
    }

}
