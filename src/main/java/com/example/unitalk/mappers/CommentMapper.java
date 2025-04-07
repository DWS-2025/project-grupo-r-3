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

    @Mapping(target = "userId", expression = "java(commentDTO.user() != null ? commentDTO.user().getId() : null)")
    @Mapping(target = "postId", expression = "java(commentDTO.post() != null ? commentDTO.post().getId() : null)")
    CommentRestDTO toRestDTO(CommentDTO commentDTO);
    List<CommentRestDTO> toRestDTOs(List<CommentDTO> commentsDTO);
}