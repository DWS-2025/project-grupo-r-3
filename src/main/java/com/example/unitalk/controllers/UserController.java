package com.example.unitalk.controllers;

import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService users;
    @Autowired
    private SubjectService subjects;

    @GetMapping
    public String showUser(Model model) {
        User user = users.getUser();
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping("/subjects/unapply")
    public String unapplySubject(@RequestParam("id") Long id, Model model) {
        User user = users.getUser();
        if (user.getSubjects() != null) {
            subjects.unnaplySubject(user, id);
        }
        return "redirect:/user";
    }
}
