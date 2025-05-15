package com.example.unitalk.restControllers;

import com.example.unitalk.DTOS.PostInputDTO;
import com.example.unitalk.DTOS.PostRestDTO;
import com.example.unitalk.DTOS.PostDTO;
import com.example.unitalk.DTOS.SubjectDTO;
import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.exceptions.ResourceNotFoundException;
import com.example.unitalk.services.PostService;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private final PostService postService;
    private final UserService userService;
    private final SubjectService subjectService;

    @Autowired
    public PostRestController(PostService postService, UserService userService, SubjectService subjectService) {
        this.postService = postService;
        this.userService = userService;
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<List<PostRestDTO>> getAllPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam Long subjectId) {
        List<PostDTO> postDTOs = postService.findByDynamicFilters(subjectId,title, description);
        List<PostRestDTO> postRestDTOs = postService.toRest(postDTOs);
        return new ResponseEntity<>(postRestDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostRestDTO> getPostById(@PathVariable Long id) {
        Optional<PostDTO> optionalPostDTO = postService.findById(id);
        if (optionalPostDTO.isEmpty()) {
            throw new ResourceNotFoundException("Post not found with ID: " + id);
        }
        PostDTO postDTO = optionalPostDTO.get();
        PostRestDTO postRestDTO = postService.toRest(postDTO);
        return new ResponseEntity<>(postRestDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody PostInputDTO postInputDTO, @RequestParam Long subjectId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        Optional<SubjectDTO> optionalSubjectDTO = subjectService.findById(subjectId);
        if (optionalSubjectDTO.isEmpty()) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }
        SubjectDTO subjectDTO = optionalSubjectDTO.get();
        PostDTO createdPost = postService.createPost(userDTO, subjectDTO, postInputDTO);
        URI location = URI.create("/api/posts/" + createdPost.id());
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @RequestParam Long subjectId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        Optional<SubjectDTO> optionalSubjectDTO = subjectService.findById(subjectId);
        if (optionalSubjectDTO.isEmpty()) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }
        SubjectDTO subjectDTO = optionalSubjectDTO.get();
        postService.deletePost(userDTO, subjectDTO, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}