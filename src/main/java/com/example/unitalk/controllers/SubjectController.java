package com.example.unitalk.controllers;

import com.example.unitalk.models.User;
import com.example.unitalk.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/subjects")
public class SubjectController {
    @Autowired
    private SubjectService subjects;
    @GetMapping
    public String showAllSubjects(Model model){
        model.addAttribute("subjects",subjects.findAll());

        return "subjects";
    }

}
