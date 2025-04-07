package com.example.unitalk.mappers;

import com.example.unitalk.DTOS.CommentDTO;
import com.example.unitalk.DTOS.CommentInputDTO;
import com.example.unitalk.DTOS.CommentRestDTO;
import com.example.unitalk.models.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDTO toDTO(Comment comment);
    List<CommentDTO> toDTOs(List<Comment> comments);
    Comment toEntity(CommentInputDTO commentDTO);

    @Mapping(target = "userId", expression = "java(comment.getUser() != null ? comment.getUser().getId() : null)")
    @Mapping(target = "postId", expression = "java(comment.getPost() != null ? comment.getPost().getId() : null)")
    CommentRestDTO toRestDTO(Comment comment);

    List<CommentRestDTO> toRestDTOs(List<Comment> comments);
}