package com.example.unitalk.restControllers;

import com.example.unitalk.DTOS.*;
import com.example.unitalk.services.CommentService;
import com.example.unitalk.services.PostService;
import com.example.unitalk.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

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
    @PostMapping("/{postId}")
    public ResponseEntity<Void> createComment(@Valid @RequestParam String commentText, @RequestParam(value = "image", required = false) MultipartFile image, @PathVariable Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        Optional<PostDTO> optionalPostDTO = postService.findById(postId);
        if(optionalPostDTO.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        PostDTO postDTO = optionalPostDTO.get();
        try {
            CommentInputDTO commentInputDTO = new CommentInputDTO(commentText, image != null ? image.getBytes() : null, image != null ? image.getOriginalFilename() : null);
            CommentDTO createdComment = commentService.createComment(userDTO,commentInputDTO, postDTO);
            URI location = URI.create("/api/comments/" + createdComment.id());
            return ResponseEntity.created(location).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
public ResponseEntity<CommentRestDTO> updateComment(
        @PathVariable Long id,
        @Valid @RequestBody CommentInputDTO commentInputDTO,
        @RequestParam Long postId) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    UserDTO userDTO = userService.getUser(username);

    Optional<PostDTO> optionalPostDTO = postService.findById(postId);
    if(optionalPostDTO.isEmpty()){
        return ResponseEntity.notFound().build();
    }
    PostDTO postDTO = optionalPostDTO.get();

    CommentDTO updatedComment = commentService.editComment(userDTO, id, commentInputDTO, postDTO);
    CommentRestDTO comment = commentService.toRest(updatedComment);
    return new ResponseEntity<>(comment, HttpStatus.OK);
}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @RequestParam Long userId, @RequestParam Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
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
                        .header("Content-Type", "image/jpeg")
                        .body(commentDTO.imageData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}