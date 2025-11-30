package com.example.unitalk.controllers;

import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.models.User;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


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
        model.addAttribute("isOwner", true);
        return "user";
    }

    @GetMapping("/{username}")
    public String showUserProfile(@PathVariable("username") String username, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = users.getUser(username);
        boolean isOwner = auth != null && auth.isAuthenticated() && username.equals(auth.getName());
        model.addAttribute("user", userDTO);
        model.addAttribute("isOwner", isOwner);
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
    public String deleteCurrentUser(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        users.deleteUser(username);
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout";
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
        UserDTO userDTOParam = new UserDTO(
                userDTO.id(),
                username,
                email,
                userDTO.profileImage(),
                userDTO.subjects(),
                userDTO.comments(),
                userDTO.posts()
        );
        users.modifyUser(userDTOParam);
        return "redirect:/user";
    }

    @PostMapping("/profile-image")
    public String uploadProfileImage(@RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return "redirect:/user";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = users.getUser(username);

        try {
            Path uploadDir = Paths.get("src/main/resources/static/images/profiles");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String currentImageUrl = userDTO.profileImage();
            if (currentImageUrl != null && !currentImageUrl.isBlank() && !"/images/default-avatar.png".equals(currentImageUrl)) {
                Path oldImagePath = Paths.get("src/main/resources/static" + currentImageUrl);
                Files.deleteIfExists(oldImagePath);
            }

            String fileName = "user-" + userDTO.id() + "-" + image.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, image.getBytes());

            String url = "/images/profiles/" + fileName;
            UserDTO updated = new UserDTO(
                    userDTO.id(),
                    userDTO.username(),
                    userDTO.email(),
                    url,
                    userDTO.subjects(),
                    userDTO.comments(),
                    userDTO.posts()
            );
            users.modifyUser(updated);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/user";
    }
}
