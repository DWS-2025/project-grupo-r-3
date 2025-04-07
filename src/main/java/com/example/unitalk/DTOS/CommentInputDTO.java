package com.example.unitalk.DTOS;

public record CommentInputDTO(
        String text,
        byte[] imageData,
        String imageName
) {}
