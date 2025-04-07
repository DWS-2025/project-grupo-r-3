package com.example.unitalk.DTOS;

import com.example.unitalk.models.Post;
import com.example.unitalk.models.User;

import java.time.LocalDateTime;

public record CommentDTO(
        Long id,
        String text,
        User user,
        Post post,
        LocalDateTime date,
        byte[] imageData,
        String imageName
) {}