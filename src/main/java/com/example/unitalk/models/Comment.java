package com.example.unitalk.models;
import com.example.unitalk.models.User;

public class Comment {
    private String text;
    private int id;
    private User user;

    public Comment(){
    }
    public Comment(User user, String text){
        this.text=text;
        this.user=user;
    }
    public Comment(User user, String text, int id){
        this.text=text;
        this.user=user;
        this.id=id;
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
}

