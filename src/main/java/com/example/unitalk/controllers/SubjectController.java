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
@RequestMapping("/subjects")
public class SubjectController {
    @Autowired
    private SubjectService subjects;
    @Autowired
    private UserService user;
    @Autowired
    private UserService userService;

    @GetMapping
    public String showAllSubjects(Model model){
        model.addAttribute("subjects",subjects.findAll());
        return "subjects";
    }
    @PostMapping("/apply")
    public String applySubject(@RequestParam("id") int id, Model model){
        Subject subject = subjects.findById(id);
        if(subject != null){
            subjects.applySubject(userService.getUser(),subject);
            model.addAttribute("status","Subject applied succesfully!");
        }
        else{
            model.addAttribute("status","Error, subject not found");
        }
        return "redirect:/subjects";
    }
    @PostMapping("/delete")
    public String deleteSubject(@RequestParam("id") int id, Model model){
        Subject subject = subjects.findById(id);
        User user = userService.getUser();
        if(subject != null){
            subjects.deleteSubject(subject);
            user.removeSubject(subject);
            model.addAttribute("status","Subject deleted succesfully!");
        }
        else{
            model.addAttribute("status","Error, subject not found");
        }
        return "redirect:/subjects";
    }
    @PostMapping("/modify")
    public String modifySubject(@RequestParam("id") int id, @RequestParam("newName") String name,Model model){
        Subject subject = subjects.findById(id);
        if(subject!=null){
            subject.setName(name);
            model.addAttribute("status","Subject deleted succesfully!");
        }
        else{
            model.addAttribute("status","Error, subject not found");
        }
        return "redirect:/subjects";

    }
    @GetMapping("/my")
    public String userSubjects(Model model){
        User user = userService.getUser();
        model.addAttribute("subjects",user.getSubjects());
        return "userSubjects";
    }


}
