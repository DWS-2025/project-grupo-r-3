package com.example.unitalk.controllers;

import com.example.unitalk.DTOS.UserDTO;
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
        UserDTO userDTO = users.getUser();
        model.addAttribute("user", userDTO);
        return "user";
    }

    @PostMapping("/subjects/unapply")
    public String unapplySubject(@RequestParam("id") Long id, Model model) {
        UserDTO userDTO = users.getUser();
        subjects.unapplySubject(userDTO, id);

        return "redirect:/user";
    }
}
