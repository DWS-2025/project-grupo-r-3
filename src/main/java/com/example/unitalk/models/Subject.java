package com.example.unitalk.models;
import java.util.ArrayList;

public class Subject {
    private String name;
    private ArrayList<User> users;

    public Subject(String name){
        this.name = name;
        this.users = new ArrayList<>();
    }

    public void addUser(User u){
        users.add(u);
    }

    public ArrayList<User> getUsers(){
        return users;
    }
}
