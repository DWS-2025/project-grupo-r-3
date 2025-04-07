package com.example.unitalk.DTOS;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.User;

import java.util.List;

public record SubjectDTO(
    Long id,
    String name,
    List<User> users,
    List<Post> postIds
    ) {
}
