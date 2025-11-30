package com.example.unitalk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.unitalk.services.UserService;
import com.example.unitalk.services.TwoFactorService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    private TwoFactorService twoFactorService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Authentication authentication, HttpServletRequest request, Model model) {
        if (authentication != null) {
            return "redirect:/";
        }
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", token.getToken());
        return "login";
    }

    @GetMapping("/login/signup")
    public String showSignupForm() {
        return "signup";
    }

    @PostMapping("/login/signup")
    public String createUser(@RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {
        try {
            userService.newUser(username, email, password);
            redirectAttributes.addFlashAttribute("message", "User created successfully! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/login/signup";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred: " + e.getMessage());
            return "redirect:/login/signup";
        }
    }

    @GetMapping("/loginerror")
    public String loginerror(Model model) {
        model.addAttribute("message", "Username or password is incorrect");
        return "login";
    }

    @GetMapping("/error")
    public String error(Model model, @ModelAttribute("message") String errorMessage) {
        model.addAttribute("message", errorMessage);
        return "error";
    }

}
