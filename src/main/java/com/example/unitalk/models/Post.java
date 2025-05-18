package com.example.unitalk.models;

import jakarta.persistence.*;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    @ManyToOne
    private Subject subject;
    @ManyToOne
    private User user;
    @OneToMany(mappedBy="post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    @ElementCollection
    private List<String> attachedFiles = new ArrayList<>();

    public Post(String title, String description, Subject subject, User user) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.user = user;
        this.comments = new ArrayList<>();
    }

    public Post() {

    }



    public String getShortDescription() {
        int maxLength = 100; // Maximum characters before truncating
        if (description.length() > maxLength) {
            return description.substring(0, maxLength) + "..."; // Truncate and add "..."
        }
        return description;
    }

    //Comments methods
    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void removeComment(Comment c) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getAttachedFiles() {
        return attachedFiles;
    }

    public void setAttachedFiles(List<String> attachedFiles) {
        this.attachedFiles = attachedFiles;
    }
}