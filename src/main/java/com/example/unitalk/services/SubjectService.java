package com.example.unitalk.services;

import com.example.unitalk.DTOS.SubjectDTO;
import com.example.unitalk.DTOS.SubjectInputDTO;
import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.mappers.UserMapper;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.SubjectRepository;
import com.example.unitalk.repository.UserRepository;
import com.example.unitalk.mappers.SubjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final SubjectMapper subjectMapper;
    private final UserMapper userMapper;


    @Autowired
    public SubjectService(SubjectRepository subjectRepository, UserRepository userRepository, SubjectMapper subjectMapper, UserMapper userMapper) {
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.subjectMapper = subjectMapper;
        this.userMapper = userMapper;
    }

    public List<SubjectDTO> findAll() {
        return subjectMapper.toDTOs(subjectRepository.findAll());
    }

    public Optional<SubjectDTO> findById(Long id) {
        return subjectRepository.findById(id).map(subjectMapper::toDTO);
    }

    public void applySubject(UserDTO userDTO, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        User user = userMapper.toEntity(userDTO);
        if (!subject.getUsers().contains(user)) {
            user.addSubject(subject);
            subject.addUser(user);
            userRepository.save(user);
        }
    }

    public void unapplySubject(UserDTO userDTO, Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        User user = userMapper.toEntity(userDTO);
        user.removeSubject(subject);
        subject.removeUser(user);
        userRepository.save(user);
    }

    public void modifySubject(SubjectInputDTO subjectDTO) {
        Subject subject = subjectRepository.findById(subjectDTO.id())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        subject.setName(subjectDTO.name());
        subjectRepository.save(subject);
    }

    public void createSubject(SubjectInputDTO subjectDTO) {
        Subject subject = subjectMapper.toEntity(subjectDTO);
        subjectRepository.save(subject);
    }

    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        for (User user : subject.getUsers()) {
            user.removeSubject(subject);
        }
        subject.getUsers().clear();
        subjectRepository.delete(subject);
    }

    @PostConstruct
    public void initDefaultSubjects() {
        if (subjectRepository.count() == 0) {
            subjectRepository.save(new Subject("Introduction to Artificial Intelligence"));
            subjectRepository.save(new Subject("Algorithms"));
            subjectRepository.save(new Subject("Arts"));
            subjectRepository.save(new Subject("Economy"));
            subjectRepository.save(new Subject("Networks"));
        }
    }

    public List<SubjectDTO> getUserSubjects() {
        User user = userRepository.findByUsername("defaultUser")
                .orElseThrow(() -> new RuntimeException("Default user not found"));
        return subjectMapper.toDTOs(user.getSubjects());
    }
}