package com.example.unitalk.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;
    private String email;
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subject> subjects;
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    public User(int id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.subjects = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    //Subjects Handling
    public void addSubject(Subject s) {
        subjects.add(s);
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public void removeSubject(Subject s) {
        subjects.remove(s);
    }

    //Comments handling
    public void addComment(Comment c) {
        comments.add(c);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void removeComment(Comment c) {
        comments.remove(c);
    }
    //Posts handling

    public List<Post> getPosts() {
        return posts;
    }

    public void addPost(Post post) {
        posts.add(post);
    }

    public void removePost(Post post) {
        posts.remove(post);
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    //Getters and Setters
    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSubjects(ArrayList<Subject> subjects) {
        this.subjects = subjects;
    }

    public void deletePostComments(int postId){
        comments.removeIf(comment -> comment.getPost().getId() == postId);
    }
}
