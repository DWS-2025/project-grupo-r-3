package com.example.unitalk.restControllers;

import com.example.unitalk.DTOS.*;
import com.example.unitalk.exceptions.ResourceNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subjects")
public class SubjectRestController {

    private final SubjectService subjectService;
    private final UserService userService;

    @Autowired
    public SubjectRestController(SubjectService subjectService, UserService userService) {
        this.subjectService = subjectService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<SubjectRestDTO>> getAllSubjects() {
        List<SubjectDTO> subjectDTOs = subjectService.findAll();
        List<SubjectRestDTO> subjectRestDTOs = subjectService.toRest(subjectDTOs);
        return new ResponseEntity<>(subjectRestDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectRestDTO> getSubjectById(@PathVariable Long id) {
        Optional<SubjectDTO> optionalSubjectDTO = subjectService.findById(id);
        if (optionalSubjectDTO.isEmpty()) {
            throw new ResourceNotFoundException("Subject not found with ID: " + id);
        }
        SubjectDTO subjectDTO = optionalSubjectDTO.get();
        SubjectRestDTO subjectRestDTO = subjectService.toRest(subjectDTO);
        return new ResponseEntity<>(subjectRestDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> createSubject(@Valid @RequestBody SubjectInputDTO subjectInputDTO) {
        SubjectDTO createdSubject = subjectService.createSubject(subjectInputDTO);
        URI location = URI.create("/api/subjects/" + createdSubject.id());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectRestDTO> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectInputDTO subjectInputDTO) {
        SubjectInputDTO modificado = new SubjectInputDTO(id,subjectInputDTO.name());
        subjectService.modifySubject(modificado);
        Optional<SubjectDTO> optionalSubjectDTO = subjectService.findById(id);
        if (optionalSubjectDTO.isEmpty()) {
            throw new ResourceNotFoundException("Subject not found with ID: " + id);
        }
        SubjectDTO subjectDTO = optionalSubjectDTO.get();
        SubjectRestDTO subjectRestDTO = subjectService.toRest(subjectDTO);
        return new ResponseEntity<>(subjectRestDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(params = "userId")
    public ResponseEntity<List<SubjectRestDTO>> getUserSubjects(@RequestParam Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.getUser(username);
        if (!userDTO.id().equals(userId)) {
            throw new ResourceNotFoundException("User ID does not match authenticated user");
        }
        List<SubjectDTO> userSubjects = subjectService.getUserSubjects();
        List<SubjectRestDTO> userSubjectRestDTOs = new ArrayList<>();
        for (SubjectDTO subjectDTO : userSubjects) {
            if (subjectDTO != null) {
                SubjectRestDTO subjectRestDTO = subjectService.toRest(subjectDTO);
                if (subjectRestDTO != null) {
                    userSubjectRestDTOs.add(subjectRestDTO);
                }
            }
        }
        return new ResponseEntity<>(userSubjectRestDTOs, HttpStatus.OK);
    }
}