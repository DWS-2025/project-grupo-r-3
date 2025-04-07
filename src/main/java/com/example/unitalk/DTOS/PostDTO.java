package com.example.unitalk.DTOS;

import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.models.Comment;
import java.time.LocalDateTime;
import java.util.List;

public record PostDTO(
        Long id,
        String title,
        String description,
        LocalDateTime date,
        Subject subject,
        User user,
        List<Comment> comments
) {}