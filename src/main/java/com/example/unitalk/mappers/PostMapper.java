package com.example.unitalk.mappers;

import com.example.unitalk.DTOS.PostDTO;
import com.example.unitalk.DTOS.PostInputDTO;
import com.example.unitalk.models.Post;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    PostDTO toDTO(Post post);

    List<PostDTO> toDTOs(List<Post> posts);
    Post toEntity(PostDTO postDTO);
    Post toEntity(PostInputDTO postDTO);

}