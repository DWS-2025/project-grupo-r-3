package com.example.unitalk.services;

import com.example.unitalk.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.unitalk.models.Subject;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Service
public class SubjectService {
    private Map<Integer, Subject> subjects = new HashMap<>();
    private static int subjectCounter = 0;
    public void applySubject(User user, Subject subject){
        if (!subject.getUsers().contains(user)) {
            user.addSubject(subject);
            subject.addUser(user);
        }
    }
    public void unnaplySubject(User user, int id){
        Subject subject = findById(id);
        if(subject!=null){
            user.removeSubject(subject);
            subject.removeUser(user);
        }
    }
    public Collection<Subject> findAll(){
        return subjects.values();
    }
    public Subject findById(int id) {
        return subjects.get(id);
    }
    public void addSubject(Subject subject){
        int uniqueID = subjectCounter;
        subjectCounter++;
        subject.setId(uniqueID);
        this.subjects.put(uniqueID,subject);
    }
    public void deleteSubject(Subject subject){
        this.subjects.remove(subject.getId());
    }
    public SubjectService(){
        addSubject(new Subject("Metodologías de desarrollo seguro"));
        addSubject(new Subject("Cálculo"));
        addSubject(new Subject("Desarrollo Web Seguro"));


    }


}
