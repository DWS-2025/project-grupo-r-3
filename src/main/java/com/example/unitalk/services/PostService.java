package com.example.unitalk.services;

import com.example.unitalk.models.Comment;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PostService {
    private final Map<Integer, Post> posts = new HashMap<>();

    public Post findById(int id) {
        return posts.get(id);
    }

    public void createPost(User user, String title, Subject subject, String description) {
        Post post = new Post(title, description, subject, user);
        user.addPost(post);
        subject.addPost(post);
        posts.put(post.getId(), post);
    }

    public void deletePost(User user, Subject subject, Post post) {
        user.removePost(post);
        subject.removePost(post);
        posts.remove(post.getId());
    }

}
