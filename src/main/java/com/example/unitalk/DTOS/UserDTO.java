package com.example.unitalk.DTOS;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.Comment;
import java.util.List;

public record UserDTO(
        Long id,
        String username,
        String email,
        List<Subject> subjects,
        List<Comment> comments,
        List<Post> posts
        )
{}
