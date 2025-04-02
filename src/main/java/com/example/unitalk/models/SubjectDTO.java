package com.example.unitalk.models;
import java.util.List;

public record SubjectDTO(
    Long id,
    String name,
    List<Long> userIds,
    List<Long> postIds
    ) {
}
