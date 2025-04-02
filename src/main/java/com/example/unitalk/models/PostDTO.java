package com.example.unitalk.models;

public record PostDTO(
    Long id,
    String title,
    String description,
    String shortDescription,
    String formattedCreatedAt,
    String subjectName,
    String userName
    ) {
}
