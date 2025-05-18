package com.example.unitalk.services;

import com.example.unitalk.DTOS.*;
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
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import java.io.IOException;
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
    private final FileStorageService fileStorageService;

    @Autowired
    public PostService(PostRepository postRepository, SubjectRepository subjectRepository, UserRepository userRepository, PostMapper postMapper, UserMapper userMapper, FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
        this.userMapper = userMapper;
        this.fileStorageService = fileStorageService;
    }

    public List<PostDTO> findAllBySubject(Long subjectId) {
        List<Post> posts = postRepository.findBySubjectId(subjectId);
        return postMapper.toDTOs(posts);
    }

    public Optional<PostDTO> findById(Long id) {
        return postRepository.findById(id).map(postMapper::toDTO);
    }

    public PostDTO createPost(UserDTO userDTO, SubjectDTO subjectDTO, PostInputDTO postDTO) {
        Subject subject = subjectRepository.findById(subjectDTO.id())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        if (!userDTO.subjects().contains(subject)) {
            throw new RuntimeException("User not enrolled in this subject");
        }
        User user = userMapper.toEntity(userDTO);
        Post post = postMapper.toEntity(postDTO);
        post.setUser(user);
        post.setSubject(subject);
        post.setTitle(Jsoup.clean(post.getTitle(), Safelist.basic()));
        post.setDescription(Jsoup.clean(post.getDescription(), Safelist.basic()));
        user.addPost(post);
        subject.addPost(post);
        postRepository.save(post);
        return postMapper.toDTO(post);
    }

    public void deletePost(String username, SubjectDTO subjectDTO, Long postId) {
        Subject subject = subjectRepository.findById(subjectDTO.id())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findByUsername(username).get();
        boolean isAdmin = user.getRoles().contains("ADMIN");
        boolean isAuthor = post.getUser().getId().equals(user.getId());

        if (!isAdmin && !isAuthor) {
            throw new RuntimeException("You are not authorized to delete this post");
        }
        if(!subject.getPosts().contains(post)){
            throw new RuntimeException("The post is not in the subject specified");
        }
        user.removePost(post);
        subject.removePost(post);
        post.getComments().clear();
        postRepository.delete(post);
    }
    public PostRestDTO toRest(PostDTO postDTO){
        return postMapper.toRestDTO(postDTO);
    }
    public List<PostRestDTO> toRest(List<PostDTO> postDTOS){
        return postMapper.toRestDTOs(postDTOS);
    }
    public List<PostDTO> findByDynamicFilters(Long subjectId, String title, String description) {
        String filteredTitle = (title != null && !title.trim().isEmpty()) ? title : null;
        String filteredDescription = (description != null && !description.trim().isEmpty()) ? description : null;

        List<Post> posts = postRepository.findBySubjectIdAndDynamicFilters(subjectId, filteredTitle, filteredDescription);
        return postMapper.toDTOs(posts);
    }

    public void addFileToPost(Long postId, String fileName) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.getAttachedFiles().add(fileName);
        postRepository.save(post);
    }

    public void removeFileFromPost(Long postId, String fileName) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.getAttachedFiles().remove(fileName);
        postRepository.save(post);
        fileStorageService.deleteFile(fileName);
    }
}