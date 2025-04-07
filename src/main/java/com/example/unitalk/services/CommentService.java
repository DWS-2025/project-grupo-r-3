package com.example.unitalk.services;

import com.example.unitalk.DTOS.CommentDTO;
import com.example.unitalk.DTOS.CommentInputDTO;
import com.example.unitalk.DTOS.PostDTO;
import com.example.unitalk.DTOS.UserDTO;
import com.example.unitalk.mappers.PostMapper;
import com.example.unitalk.mappers.UserMapper;
import com.example.unitalk.models.Comment;
import com.example.unitalk.mappers.CommentMapper;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.CommentRepository;
import com.example.unitalk.repository.PostRepository;
import com.example.unitalk.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    public Optional<CommentDTO> findById(Long commentId) {
        return commentRepository.findById(commentId).map(commentMapper::toDTO);
    }

    public List<CommentDTO> findAllByPost(Long postId) {
        return commentRepository.findAll().stream()
                .filter(comment -> comment.getPost() != null && comment.getPost().getId().equals(postId))
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void createComment(UserDTO userDTO, CommentInputDTO commentInputDTO, PostDTO postDTO) {
        if (commentInputDTO.text() == null || commentInputDTO.text().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }
        Comment comment = commentMapper.toEntity(commentInputDTO);
        User user = userMapper.toEntity(userDTO);
        Post post = postMapper.toEntity(postDTO);
        comment.setUser(user);
        comment.setPost(post);
        user.addComment(comment);
        post.addComment(comment);
        commentRepository.save(comment);
    }

    public void editComment(Long commentId, CommentInputDTO commentInputDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setText(commentInputDTO.text());
        commentRepository.save(comment);
    }
    @Transactional
    public void deleteComment(UserDTO userDTO, Long commentId, PostDTO postDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findById(userDTO.id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postDTO.id())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!comment.getUser().getId().equals(user.getId()) || !comment.getPost().getId().equals(post.getId())) {
            throw new RuntimeException("User not authorized to delete this comment or comment not associated with the post");
        }
        user.removeComment(comment);
        post.removeComment(comment);
        commentRepository.delete(comment);
    }
}