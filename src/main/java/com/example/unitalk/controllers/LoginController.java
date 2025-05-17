package com.example.unitalk.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class LoginController {
    @GetMapping("/")
    public String index() {
        return "index";
    }


    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/loginerror")
    public String loginerror(Model model) {
        model.addAttribute("message", "Username or password is incorrect");
        return "login";
    }
    @GetMapping("/error")
    public String error(Model model, @ModelAttribute("message") String errorMessage) {
        model.addAttribute("message",  errorMessage);
        return "error";
    }

}
