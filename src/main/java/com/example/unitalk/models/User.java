package com.example.unitalk.models;
import java.util.ArrayList;

public class User {
    private int id;
    private String username;
    private String email;
    private ArrayList<Subject> subjects;

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
}
