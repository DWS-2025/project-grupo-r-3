package com.example.unitalk.models;
import com.example.unitalk.models.*;
import jakarta.persistence.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String text;
    @ManyToOne
    private User user;
    @ManyToOne
    private Post post;
    private final LocalDateTime date;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageData;

    private String imageName;

    public Comment(User user, String text, Post post, byte[] imageData, String imageName) {
        this.user = user;
        this.text = text;
        this.post = post;
        this.imageData = imageData;
        this.imageName = imageName;
        this.date = LocalDateTime.now();
    }

    public Comment(String text, User user, Post post) {
        this.user = user;
        this.text = text;
        this.post = post;
        this.imageData = null;
        this.imageName = null;
        this.date = LocalDateTime.now();
    }

    public Comment() {
        this.date = LocalDateTime.now();
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}

