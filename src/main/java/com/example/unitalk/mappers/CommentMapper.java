package com.example.unitalk.mappers;

import com.example.unitalk.DTOS.CommentDTO;
import com.example.unitalk.DTOS.CommentInputDTO;
import com.example.unitalk.models.Comment;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDTO toDTO(Comment comment);

    List<CommentDTO> toDTOs(List<Comment> comments);

    Comment toEntity(CommentInputDTO commentDTO);
}