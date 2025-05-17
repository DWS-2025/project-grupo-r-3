package com.example.unitalk.controllers;

import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.models.User;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = users.getUser(username);
        model.addAttribute("user", userDTO);
        return "user";
    }

    @PostMapping("/subjects/unapply")
    public String unapplySubject(@RequestParam("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        subjects.unapplySubject(username, id);

        return "redirect:/user";
    }

    @PostMapping("/delete")
    public String deleteCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        users.deleteUser(username);
        return "redirect:/logout";
    }
    
    @GetMapping("/modify")
    public String modifyUser(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = users.getUser(username);
        model.addAttribute("user", userDTO);
        return "modifyUser";
    }

    @PostMapping("/set")
    public String setUser(@RequestParam(value = "username", required = false) String username,
                          @RequestParam(value = "email", required = false) String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String u = auth.getName();
        UserDTO userDTO = users.getUser(u);
        UserDTO userDTOParam = new UserDTO(userDTO.id(), username, email, null, null, null);
        users.modifyUser(userDTOParam);
        return "redirect:/user";
    }
}
