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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
    public String showAllSubjects(Model model, Authentication authentication) {
        model.addAttribute("subjects", subjectService.findAll());
        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
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
    public ResponseEntity<Void> modifySubject(@RequestParam("id") Long id, @RequestParam("newName") String newName, RedirectAttributes redirectAttributes, Model model) {
        System.out.println("Received POST /subjects/modify with id: " + id + ", newName: " + newName);
        try {
            if (newName == null || newName.trim().isEmpty()) {
                System.out.println("Error: Subject name is empty");
                redirectAttributes.addFlashAttribute("error", "Subject name cannot be empty.");
            } else {
                SubjectInputDTO subjectDTO = new SubjectInputDTO(id, newName);
                subjectService.modifySubject(subjectDTO);
                System.out.println("Subject modified successfully");
                redirectAttributes.addFlashAttribute("status", "Subject modified successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error modifying subject: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error modifying subject: " + e.getMessage());
        }
        model.asMap().clear();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/subjects")
                .build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public RedirectView createSubject(@RequestParam("name") String name, RedirectAttributes redirectAttributes, Model model) {
        System.out.println("Received POST /subjects/create with name: " + name);
        try {
            if (name == null || name.trim().isEmpty()) {
                System.out.println("Error: Subject name is empty");
                redirectAttributes.addFlashAttribute("error", "Subject name cannot be empty.");
                return new RedirectView("/subjects", true);
            }
            SubjectInputDTO subjectDTO = new SubjectInputDTO(null, name);
            subjectService.createSubject(subjectDTO);
            System.out.println("Subject created successfully");
            redirectAttributes.addFlashAttribute("status", "Subject created successfully!");
        } catch (Exception e) {
            System.out.println("Error creating subject: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error creating subject: " + e.getMessage());
        }
        return new RedirectView("/subjects", true);
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
