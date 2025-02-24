package com.example.unitalk.models;
import com.example.unitalk.models.User;

public class Comment {
    private String text;
    private Long id;
    private User user;
    private String title;

    public Comment(){
    }
    public Comment(User user, String title, String text){
        this.text=text;
        this.user=user;
        this.title=title;
    }
    public Comment(User user, String title, String text, long id){
        this.text=text;
        this.user=user;
        this.title=title;
        this.id=id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

