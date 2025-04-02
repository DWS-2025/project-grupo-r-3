package com.example.unitalk.models;
import java.time.LocalDateTime;

public record PostDTO(
    Long id,
    String title,
    String description,
    LocalDateTime date,
    String subjectName,
    String userName
    ) {
}
