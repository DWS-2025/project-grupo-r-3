package com.example.unitalk.models;
import com.example.unitalk.models.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

public class Comment {
    private String text;
    private int id;
    private User user;
    private Post post;
    private final LocalDateTime date;

    public Comment(User user, String text, Post post, int id){
        this.text=text;
        this.user=user;
        this.date = LocalDateTime.now();
        this.post = post;
        this.id=id;

    }
    // Obtain date formatted as dd/MM/yyyy
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public LocalDateTime getDate() {
        return date;
    }
}

