package com.example.unitalk.models;
import java.time.LocalDateTime;

public record CommentDTO(
    Long id,
    String text,
    String userName,
    Long postId,
    LocalDateTime date,
    byte[] imageData,
    String imageName
    ){
}
