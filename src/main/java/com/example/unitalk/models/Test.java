package com.example.unitalk.models;
import com.example.unitalk.services.SubjectService;

public class Test {
    public static void main(String[] args) {
        User user = new User(0, "pabaal", "correo");
        Subject s1 = new Subject("Estructura de Datos");
        System.out.println(s1.getUsers());
        s1.addUser(user);
        System.out.println(s1.getUsers());
        user.addSubject(s1);
        System.out.println(user.getSubjects());
    }
}
