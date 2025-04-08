package com.example.unitalk.mappers;

import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.DTOS.UserRestDTO;
import com.example.unitalk.models.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);

    @Mapping(target = "subjectIds", source = "subjects", qualifiedByName = "mapSubjectsToIds")
    @Mapping(target = "commentIds", source = "comments", qualifiedByName = "mapCommentsToIds")
    @Mapping(target = "postIds", source = "posts", qualifiedByName = "mapPostsToIds")
    UserRestDTO toRestDTO(UserDTO userDTO);

    @Named("mapSubjectsToIds")
    default List<Long> mapSubjectsToIds(List<Subject> subjects) {
        if (subjects == null) {
            return null;
        }
        return subjects.stream().map(Subject::getId).collect(Collectors.toList());
    }

    @Named("mapCommentsToIds")
    default List<Long> mapCommentsToIds(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream().map(Comment::getId).collect(Collectors.toList());
    }

    @Named("mapPostsToIds")
    default List<Long> mapPostsToIds(List<Post> posts) {
        if (posts == null) {
            return null;
        }
        return posts.stream().map(Post::getId).collect(Collectors.toList());
    }
}