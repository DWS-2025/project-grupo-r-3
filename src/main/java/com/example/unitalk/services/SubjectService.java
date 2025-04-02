package com.example.unitalk.services;

import com.example.unitalk.models.User;
import com.example.unitalk.models.Subject;
import com.example.unitalk.repository.SubjectRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public void applySubject(User user, Subject subject) {
        if (!subject.getUsers().contains(user)) {
            user.addSubject(subject);
            subject.addUser(user);
            subjectRepository.save(subject);
        }
    }

    public void unnaplySubject(User user, Long id) {
        Optional<Subject> optionalSubject = findById(id);
        if (optionalSubject.isPresent()) {
            Subject subject = optionalSubject.get();
            user.removeSubject(subject);
            subject.removeUser(user);
            subjectRepository.save(subject);
        }
    }

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    public void addSubject(Subject subject) {
        subjectRepository.save(subject);
    }

    public void deleteSubject(Subject subject) {
        for (User user : subject.getUsers()) {
            user.removeSubject(subject);
        }
        subject.getUsers().clear();
        subjectRepository.delete(subject);
    }
    @PostConstruct
    public void initDefaultSubjects() {
        if (subjectRepository.findAll().isEmpty()) {
            addSubject(new Subject("Introduction to Artificial Intelligence"));
            addSubject(new Subject("Algorithms"));
            addSubject(new Subject("Arts"));
        }
    }
}