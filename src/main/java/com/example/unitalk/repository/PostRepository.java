package com.example.unitalk.repository;

import com.example.unitalk.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findBySubjectId(Long subjectId);
}