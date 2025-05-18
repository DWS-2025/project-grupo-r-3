package com.example.unitalk.restControllers;

import com.example.unitalk.DTOS.PostInputDTO;
import com.example.unitalk.DTOS.PostRestDTO;
import com.example.unitalk.DTOS.PostDTO;
import com.example.unitalk.DTOS.SubjectDTO;
import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.exceptions.ResourceNotFoundException;
import com.example.unitalk.services.FileStorageService;
import com.example.unitalk.services.PostService;
import com.example.unitalk.services.SubjectService;
import com.example.unitalk.services.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
    private FileStorageService fileStorageService;

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
        Optional<SubjectDTO> optionalSubjectDTO = subjectService.findById(subjectId);
        if (optionalSubjectDTO.isEmpty()) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }
        SubjectDTO subjectDTO = optionalSubjectDTO.get();
        postService.deletePost(username, subjectDTO, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/{postId}/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long postId, @PathVariable String fileName) {
        Optional<PostDTO> optionalPost = postService.findById(postId);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        PostDTO post = optionalPost.get();
        if (!post.attachedFiles().contains(fileName)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Resource file = fileStorageService.loadFileAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @DeleteMapping("/{postId}/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable Long postId, @PathVariable String fileName) {
        try {
            Optional<PostDTO> optionalPost = postService.findById(postId);
            if (optionalPost.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            PostDTO post = optionalPost.get();
            if (!post.attachedFiles().contains(fileName)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("File does not belong to this post");
            }

            postService.removeFileFromPost(postId, fileName);
            fileStorageService.deleteFile(fileName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}