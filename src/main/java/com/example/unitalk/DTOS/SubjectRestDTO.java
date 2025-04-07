package com.example.unitalk.DTOS;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.User;

import java.util.List;

public record SubjectRestDTO(
        Long id,
        String name,
        Long userId,
        List<Post> posts
) {
}
