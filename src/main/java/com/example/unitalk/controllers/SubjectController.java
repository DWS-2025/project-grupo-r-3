package com.example.unitalk.controllers;

import com.example.unitalk.models.User;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubjectController {
    @GetMapping ("/subjects")
    public String showUserSubjects(Model model, User user){

        return "subjects";
    }
}
