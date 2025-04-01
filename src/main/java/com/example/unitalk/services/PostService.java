package com.example.unitalk.services;

import com.example.unitalk.models.Comment;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public void createPost(User user, String title, Subject subject, String description) {
        Post post = new Post(title, description, subject, user);
        user.addPost(post);
        subject.addPost(post);
        postRepository.save(post);
    }

    public void deletePost(User user, Subject subject, Post post) {
        user.removePost(post);
        subject.removePost(post);
        postRepository.delete(post);
    }

    public void deletePostComments(List<Comment> comments) {
        comments.clear();
    }
}
