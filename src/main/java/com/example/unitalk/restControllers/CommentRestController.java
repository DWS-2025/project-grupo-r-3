package com.example.unitalk.restControllers;

import com.example.unitalk.DTOS.CommentDTO;
import com.example.unitalk.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments(@RequestParam Long postId) {
        List<CommentDTO> comments = commentService.findAllByPost(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable Long id) {
        Optional<CommentDTO> optionalComment = commentService.findById(id);
        if(optionalComment.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        CommentDTO comment = optionalComment.get();
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }


}