package com.example.unitalk.models;
import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String name;
    private List<User> users = new ArrayList<>();
    private int id;

    public Subject(String name){
        this.name = name;
        this.users = new ArrayList<>();
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public List<User> getUsers(){
        return users;
    }
    public void setUsers(List<User> users){
        this.users = users;
    }
    public void addUser(User user){
        this.users.add(user);
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
}
