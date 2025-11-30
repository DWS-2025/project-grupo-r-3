package com.example.unitalk.mappers;

import com.example.unitalk.DTOS.*;
import com.example.unitalk.models.Comment;
import com.example.unitalk.models.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface PostMapper {
    PostDTO toDTO(Post post);
    List<PostDTO> toDTOs(List<Post> posts);
    Post toEntity(PostDTO postDTO);
    Post toEntity(PostInputDTO postDTO);

    @Mapping(target = "subjectId", expression = "java(postDTO.subject() != null ? postDTO.subject().getId() : null)")
    @Mapping(target = "userId", expression = "java(postDTO.user() != null ? postDTO.user().getId() : null)")
    @Mapping(target = "comments", qualifiedByName = "mapCommentsToRestDTOs")
    PostRestDTO toRestDTO(PostDTO postDTO);

    List<PostRestDTO> toRestDTOs(List<PostDTO> postsDTO);

    @Named("mapCommentsToRestDTOs")
    default List<CommentRestDTO> mapCommentsToRestDTOs(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        CommentMapper commentMapper = PostMapperInstance.commentMapper(); 
        return comments.stream()
                .map(comment -> commentMapper.toRestDTO(commentMapper.toDTO(comment)))
                .collect(Collectors.toList());
    }
}class PostMapperInstance {
    private static final CommentMapper commentMapper = new CommentMapperImpl(); 

    public static CommentMapper commentMapper() {
        return commentMapper;
    }
}


