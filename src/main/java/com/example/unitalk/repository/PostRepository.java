package com.example.unitalk.repository;

import com.example.unitalk.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findBySubjectId(Long subjectId);

    @Query("SELECT p FROM Post p WHERE p.subject.id = :subjectId AND (:title IS NULL OR p.title LIKE %:title%) AND (:description IS NULL OR p.description LIKE %:description%)")
    List<Post> findBySubjectIdAndDynamicFilters(@Param("subjectId") Long subjectId, @Param("title") String title, @Param("description") String description);
}