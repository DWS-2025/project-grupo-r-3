package com.example.unitalk.services;

import com.example.unitalk.DTOS.PostDTO;
import com.example.unitalk.DTOS.PostInputDTO;
import com.example.unitalk.DTOS.SubjectDTO;
import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.mappers.UserMapper;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.Subject;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.PostRepository;
import com.example.unitalk.repository.SubjectRepository;
import com.example.unitalk.repository.UserRepository;
import com.example.unitalk.mappers.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final UserMapper userMapper;

    @Autowired
    public PostService(PostRepository postRepository, SubjectRepository subjectRepository, UserRepository userRepository, PostMapper postMapper, UserMapper userMapper) {
        this.postRepository = postRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
        this.userMapper = userMapper;
    }

    public List<PostDTO> findAllBySubject(Long subjectId) {
        return postRepository.findAll().stream()
                .filter(post -> post.getSubject() != null && post.getSubject().getId().equals(subjectId))
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<PostDTO> findById(Long id) {
        return postRepository.findById(id).map(postMapper::toDTO);
    }

    public PostDTO createPost(UserDTO userDTO, SubjectDTO subjectDTO, PostInputDTO postDTO) {
        Subject subject = subjectRepository.findById(subjectDTO.id())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        if (!userMapper.toEntity(userDTO).getSubjects().contains(subject)) {
            throw new RuntimeException("User not enrolled in this subject");
        }
        User user = userMapper.toEntity(userDTO);
        Post post = postMapper.toEntity(postDTO);
        post.setUser(user);
        post.setSubject(subject);
        user.addPost(post);
        subject.addPost(post);
        postRepository.save(post);
        return postMapper.toDTO(post);
    }

    public void deletePost(UserDTO userDTO, SubjectDTO subjectDTO, Long postId) {
        Subject subject = subjectRepository.findById(subjectDTO.id())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userMapper.toEntity(userDTO);
        if (!user.getSubjects().contains(subject) || !post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User not authorized to delete this post");
        }
        user.removePost(post);
        subject.removePost(post);
        post.getComments().clear();
        postRepository.delete(post);
    }

    public void deletePostComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.getComments().clear();
        postRepository.save(post);
    }
}