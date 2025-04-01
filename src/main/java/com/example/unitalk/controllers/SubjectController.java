package com.example.unitalk.controllers;

import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.SubjectRepository;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/subjects")
public class SubjectController {
    @Autowired
    private SubjectService subjects;
    @Autowired
    private UserService user;
    @Autowired
    private UserService userService;
    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public String showAllSubjects(Model model) {
        model.addAttribute("subjects", subjects.findAll());
        return "subjects";
    }

    @PostMapping("/apply")
    public String applySubject(@RequestParam("id") Long id, Model model) {
        Optional<Subject> optionalSubject = subjects.findById(id);
        if (optionalSubject.isPresent()) {
            Subject subject = optionalSubject.get();
            subjects.applySubject(userService.getUser(), subject);
            model.addAttribute("status", "Subject applied succesfully!");
        } else {
            model.addAttribute("status", "Error, subject not found");
        }
        return "redirect:/subjects";
    }

    @PostMapping("/delete")
    public String deleteSubject(@RequestParam("id") Long id, Model model) {
        Optional<Subject> optionalSubject = subjects.findById(id);
        User user = userService.getUser();
        if (optionalSubject.isPresent()) {
            Subject subject = optionalSubject.get();
            subjects.deleteSubject(subject);
            user.removeSubject(subject);
            model.addAttribute("status", "Subject deleted succesfully!");
        } else {
            model.addAttribute("status", "Error, subject not found");
        }
        return "redirect:/subjects";
    }

    @PostMapping("/modify")
    public String modifySubject(@RequestParam("id") Long id, @RequestParam("newName") String name, Model model) {
        Optional<Subject> optionalSubject = subjects.findById(id);
        if (optionalSubject.isPresent()) {
            Subject subject = optionalSubject.get();
            subject.setName(name);
            model.addAttribute("status", "Subject deleted succesfully!");
        } else {
            model.addAttribute("status", "Error, subject not found");
        }
        return "redirect:/subjects";

    }
    @PostMapping("/create")
    public String createSubject(@RequestParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be empty.");
        }
        Subject subject = new Subject(name);
        subjects.addSubject(subject);

        return "redirect:/subjects";
    }


    @GetMapping("/my")
    public String userSubjects(Model model) {
        User user = userService.getUser();
        model.addAttribute("subjects", user.getSubjects());
        return "userSubjects";
    }
    @PostConstruct
    public void init() {
        if (subjectRepository.count() == 0) {
            subjectRepository.save(new Subject("Introduction to Artificial Intelligence"));
            subjectRepository.save(new Subject("Algorithms"));
            subjectRepository.save(new Subject("Arts"));
        }
    }
}
