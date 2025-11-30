package com.example.unitalk.controllers;

import com.example.unitalk.models.User;
import com.example.unitalk.services.TwoFactorService;
import com.example.unitalk.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/2fa")
public class TwoFactorController {

    @Autowired
    private TwoFactorService twoFactorService;

    @Autowired
    private UserService userService;

    @GetMapping("/setup")
    public String showSetupPage(Authentication authentication, HttpServletRequest request, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/";
        }

        // Generate new secret if not already set
        String secret = user.getTwoFactorSecret();
        if (secret == null || secret.isEmpty()) {
            secret = twoFactorService.generateNewSecret();
            user.setTwoFactorSecret(secret);
            userService.save(user);
        }

        String otpAuthUrl = twoFactorService.generateOtpAuthUrl(username, secret);

        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", token.getToken());
        model.addAttribute("secret", secret);
        model.addAttribute("otpAuthUrl", otpAuthUrl);
        model.addAttribute("username", username);
        model.addAttribute("isTwoFactorEnabled", user.isTwoFactorEnabled());

        return "setup_2fa";
    }

    @PostMapping("/enable")
    public String enableTwoFactor(@RequestParam("code") int code,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/2fa/setup";
        }

        String secret = user.getTwoFactorSecret();
        if (secret == null || secret.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "2FA not set up. Please scan the QR code first.");
            return "redirect:/2fa/setup";
        }

        // Verify the code
        if (twoFactorService.isOtpValid(secret, code)) {
            user.setTwoFactorEnabled(true);
            userService.save(user);
            redirectAttributes.addFlashAttribute("message", "Two-Factor Authentication enabled successfully!");
            return "redirect:/user";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid verification code. Please try again.");
            return "redirect:/2fa/setup";
        }
    }

    @PostMapping("/disable")
    public String disableTwoFactor(@RequestParam("code") int code,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/user";
        }

        String secret = user.getTwoFactorSecret();
        if (secret == null || secret.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "2FA is not set up.");
            return "redirect:/user";
        }

        // Verify the code before disabling
        if (twoFactorService.isOtpValid(secret, code)) {
            user.setTwoFactorEnabled(false);
            userService.save(user);
            redirectAttributes.addFlashAttribute("message", "Two-Factor Authentication disabled successfully!");
            return "redirect:/user";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid verification code. Cannot disable 2FA.");
            return "redirect:/user";
        }
    }

    @GetMapping("/verify")
    public String showVerifyPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("2FA_USERNAME") == null) {
            return "redirect:/login";
        }

        String username = (String) session.getAttribute("2FA_USERNAME");
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", token.getToken());
        model.addAttribute("username", username);

        return "verify_2fa";
    }

    @PostMapping("/verify")
    public String verifyTwoFactor(@RequestParam("code") int code,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("2FA_USERNAME") == null) {
            redirectAttributes.addFlashAttribute("error", "Session expired. Please login again.");
            return "redirect:/login";
        }

        String username = (String) session.getAttribute("2FA_USERNAME");
        User user = userService.findByUsername(username).orElse(null);

        if (user == null || !user.isTwoFactorEnabled()) {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials");
            return "redirect:/login";
        }

        String secret = user.getTwoFactorSecret();
        if (twoFactorService.isOtpValid(secret, code)) {
            // Code is valid - restore authentication to SecurityContext
            Authentication authentication = (Authentication) session.getAttribute("2FA_AUTHENTICATION");
            if (authentication != null) {
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // Mark session as 2FA authenticated and clean up
            session.setAttribute("2FA_AUTHENTICATED", true);
            session.removeAttribute("2FA_USERNAME");
            session.removeAttribute("2FA_AUTHENTICATION");

            // Redirect to home page
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid verification code");
            return "redirect:/2fa/verify";
        }
    }
}
