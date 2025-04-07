package com.example.unitalk.DTOS;
import java.time.LocalDateTime;

public record CommentRestDTO(
        Long id,
        String text,
        Long userId,
        Long postId,
        LocalDateTime date,
        byte[] imageData,
        String imageName
) {}