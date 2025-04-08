package com.example.unitalk.restControllers;

import com.example.unitalk.DTOS.*;
import com.example.unitalk.services.CommentService;
import com.example.unitalk.services.PostService;
import com.example.unitalk.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<Page<CommentRestDTO>> getAllComments(
            @RequestParam Long postId,
            Pageable pageable) {

        Page<CommentDTO> commentPage = commentService.findAllByPost(postId, pageable);
        Page<CommentRestDTO> restPage = commentPage.map(commentService::toRest);

        return ResponseEntity.ok(restPage);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CommentRestDTO> getComment(@PathVariable Long id) {
        Optional<CommentDTO> optionalComment = commentService.findById(id);
        if(optionalComment.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        CommentDTO commentDTO = optionalComment.get();
        CommentRestDTO comment = commentService.toRest(commentDTO);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Void> createComment(@Valid @RequestBody CommentInputDTO commentInputDTO, @RequestParam Long postId) {
        UserDTO userDTO = userService.getUser();
        Optional<PostDTO> optionalPostDTO = postService.findById(postId);
        if(optionalPostDTO.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        PostDTO postDTO = optionalPostDTO.get();
        CommentDTO createdComment = commentService.createComment(userDTO,commentInputDTO, postDTO);
        URI location = URI.create("/api/comments/" + createdComment.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentRestDTO> updateComment(@PathVariable Long id, @Valid @RequestBody CommentInputDTO commentInputDTO) {
        CommentDTO updatedComment = commentService.editComment(id, commentInputDTO);
        CommentRestDTO comment = commentService.toRest(updatedComment);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @RequestParam Long userId, @RequestParam Long postId) {
        UserDTO userDTO = userService.getUser();
        Optional<PostDTO> optionalPostDTO = postService.findById(postId);
        if(optionalPostDTO.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        PostDTO postDTO = optionalPostDTO.get();
        commentService.deleteComment(userDTO, id, postDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getCommentImage(@PathVariable Long id) {
        return commentService.findById(id)
                .filter(commentDTO -> commentDTO.imageData() != null)
                .map(commentDTO -> ResponseEntity.ok()
                        .header("Content-Type", "image/jpeg") // Ajusta según el tipo de imagen (puede ser "image/png", etc.)
                        .body(commentDTO.imageData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}