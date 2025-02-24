package com.example.unitalk.models;
import org.yaml.snakeyaml.comments.CommentEventsCollector;

import javax.lang.model.type.ArrayType;
import java.util.ArrayList;

public class User {
    private int id;
    private String username;
    private String email;
    private ArrayList<Subject> subjects;
    private ArrayList<Comment> comments;

    public User(int id, String username, String email){
        this.id = id;
        this.username = username;
        this.email = email;
        this.subjects = new ArrayList<>();
    }

    public void addSubject(Subject s){
        subjects.add(s);
    }

    public ArrayList<Subject> getSubjects(){
        return subjects;
    }
    public String getUsername(){
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

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
    public void addComment(Comment c){
        comments.add(c);
    }
    public void removeComment(Comment c){
        comments.remove(c);
    }
}
