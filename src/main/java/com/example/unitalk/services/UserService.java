package com.example.unitalk.services;
import com.example.unitalk.models.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private User user;

    public UserService() {
        this.user = new User(1, "defaultUser", "usuario@example.com");
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }
}
