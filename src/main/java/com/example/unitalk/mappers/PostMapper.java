package com.example.unitalk.mappers;
import com.example.unitalk.DTOS.PostRestDTO;
import com.example.unitalk.DTOS.PostDTO;
import com.example.unitalk.DTOS.PostInputDTO;
import com.example.unitalk.models.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper(componentModel = "spring")
public interface PostMapper {

    PostDTO toDTO(Post post);
    List<PostDTO> toDTOs(List<Post> posts);
    Post toEntity(PostDTO postDTO);
    Post toEntity(PostInputDTO postDTO);

    // Methods for rest
    @Mapping(target = "subjectId", expression = "java(post.getSubject() != null ? post.getSubject().getId() : null)")
    @Mapping(target = "userId", expression = "java(post.getUser() != null ? post.getUser().getId() : null)")
    PostRestDTO toRestDTO(Post post);
    List<PostRestDTO> toRestDTOs(List<Post> posts);


    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostRestDTO postRestDTO);
}