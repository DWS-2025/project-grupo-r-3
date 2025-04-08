package com.example.unitalk.DTOS;

import java.util.List;

public record SubjectRestDTO(
        Long id,
        String name,
        List<Long> userIds
) {}