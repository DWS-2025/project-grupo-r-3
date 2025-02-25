package com.example.unitalk.models;
import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String name;
    private List<User> users;
    private List<Post> posts;
    private int id;

    public Subject(String name){
        this.name = name;
        this.users = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
