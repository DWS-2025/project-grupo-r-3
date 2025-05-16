package com.example.unitalk.controllers;

import com.example.unitalk.DTOS.SubjectDTO;
import com.example.unitalk.DTOS.SubjectInputDTO;
import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.SubjectRepository;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@EnableMethodSecurity
@RequestMapping("/subjects")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String showAllSubjects(Model model) {
        model.addAttribute("subjects", subjectService.findAll());
        return "subjects";
    }

    @PostMapping("/apply")
    public String applySubject(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        subjectService.applySubject(userDTO, id);
        redirectAttributes.addFlashAttribute("status", "Subject applied successfully!");
        return "redirect:/subjects";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public String deleteSubject(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        subjectService.deleteSubject(id);
        redirectAttributes.addFlashAttribute("status", "Subject deleted successfully!");
        return "redirect:/subjects";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/modify")
    public String modifySubject(@RequestParam("id") Long id, @RequestParam("newName") String newName, RedirectAttributes redirectAttributes) {
        SubjectInputDTO subjectDTO = new SubjectInputDTO(id, newName);
        subjectService.modifySubject(subjectDTO);
        redirectAttributes.addFlashAttribute("status", "Subject modified successfully!");
        return "redirect:/subjects";
    }

    @PostMapping("/create")
    public String createSubject(@RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name cannot be empty.");
        }
        SubjectInputDTO subjectDTO = new SubjectInputDTO(null, name);
        subjectService.createSubject(subjectDTO);
        redirectAttributes.addFlashAttribute("status", "Subject created successfully!");
        return "redirect:/subjects";
    }


    @GetMapping("/my")
    public String userSubjects(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        List<SubjectDTO> userSubjects = subjectService.getUserSubjects(username);
        model.addAttribute("subjects", userSubjects);
        return "userSubjects";
    }
}
