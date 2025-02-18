package com.example.unitalk.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.example.unitalk.models.User;



@Service
public class UserService {
    public void newUser(int id, String username, String email){}
    public User getUserById(int id){}
    public User getUserByUsername(String username){}
    public void deleteUser(int id){}
    public void modifyUser(int id){}
}