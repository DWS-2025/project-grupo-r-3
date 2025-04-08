package com.example.unitalk.mappers;

import com.example.unitalk.models.User;
import com.example.unitalk.DTOS.SubjectDTO;
import com.example.unitalk.DTOS.SubjectInputDTO;
import com.example.unitalk.DTOS.SubjectRestDTO;
import com.example.unitalk.models.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    SubjectDTO toDTO(Subject subject);
    List<SubjectDTO> toDTOs(List<Subject> subjects);
    Subject toEntity(SubjectInputDTO subjectDTO);
    @Mapping(target = "userIds", expression = "java(mapUserIds(subjectDTO))")
    SubjectRestDTO toRestDTO(SubjectDTO subjectDTO);
    List<SubjectRestDTO> toRestDTOs(List<SubjectDTO> subjectDTOs);

    default List<Long> mapUserIds(SubjectDTO dto) {
        if (dto.users() == null) return List.of();
        return dto.users().stream().map(User::getId).collect(Collectors.toList());
    }
}