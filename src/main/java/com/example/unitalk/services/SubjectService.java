package com.example.unitalk.services;

import com.example.unitalk.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.unitalk.models.Subject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Service
public class SubjectService {
    private Map<Integer, Subject> subjects = new HashMap<>();
    private static int subjectCounter = 0;
    public void applySubject(User user, Subject subject){
        user.addSubject(subject);
        subject.addUser(user);
    }
    public Collection<Subject> findAll(){
        return subjects.values();
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


}
