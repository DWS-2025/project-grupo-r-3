package com.example.unitalk.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Post {
    private int id;
    private String title;
    private String description;
    private Subject subject;
    private User user;
    private List<Comment> comments;
    private static int idCounter = 0;

    public Post(String title, String description, Subject subject, User user) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.user = user;
        this.comments = new ArrayList<>();
        this.id = idCounter++;
    }

    //Comments methods
    public List<Comment> getComments() {
        return comments;
    }
    public void addComment(Comment comment){
        comments.add(comment);
    }
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    public void removeComment(Comment c){
        comments.remove(c);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
