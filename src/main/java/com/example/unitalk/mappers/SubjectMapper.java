package com.example.unitalk.mappers;

import com.example.unitalk.DTOS.SubjectDTO;
import com.example.unitalk.DTOS.SubjectInputDTO;
import com.example.unitalk.models.Subject;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    SubjectDTO toDTO(Subject subject);
    List<SubjectDTO> toDTOs(List<Subject> subjects);
    Subject toEntity(SubjectInputDTO subjectDTO);
}