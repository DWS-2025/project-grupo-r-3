package com.example.unitalk.DTOS;

import java.util.List;

public record UserRestDTO(
        Long id,
        String username,
        String email,
        List<Long> subjectIds, 
        List<Long> commentIds, 
        List<Long> postIds    
) {}