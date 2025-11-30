package com.example.unitalk.services;

import com.example.unitalk.DTOS.*;
import com.example.unitalk.mappers.PostMapper;
import com.example.unitalk.mappers.UserMapper;
import com.example.unitalk.models.Comment;
import com.example.unitalk.mappers.CommentMapper;
import com.example.unitalk.models.Post;
import com.example.unitalk.models.User;
import com.example.unitalk.repository.CommentRepository;
import com.example.unitalk.repository.PostRepository;
import com.example.unitalk.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    public Page<CommentDTO> findAllByPost(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable)
                .map(commentMapper::toDTO);
    }

    public CommentDTO createComment(UserDTO userDTO, CommentInputDTO commentInputDTO, PostDTO postDTO) {
    if (commentInputDTO.text() == null || commentInputDTO.text().trim().isEmpty()) {
        throw new IllegalArgumentException("Comment cannot be empty.");
    }

    Comment comment = commentMapper.toEntity(commentInputDTO);
    comment.setText(Jsoup.clean(comment.getText(), Safelist.basic()));

    // Cargar entidades gestionadas
    User user = userRepository.findById(userDTO.id())
            .orElseThrow(() -> new RuntimeException("User not found"));
    Post post = postRepository.findById(postDTO.id())
            .orElseThrow(() -> new RuntimeException("Post not found"));

    comment.setUser(user);
    comment.setPost(post);
    user.addComment(comment);
    post.addComment(comment);

    if (commentInputDTO.imageData() != null && commentInputDTO.imageData().length > 0) {
        try {
            byte[] resizedImageData = resizeImage(commentInputDTO.imageData(), 800, 600);
            comment.setImageData(resizedImageData);
        } catch (IOException e) {
            throw new RuntimeException("Failed to resize image: " + e.getMessage(), e);
        }
    }

    Comment savedComment = commentRepository.save(comment);
    return commentMapper.toDTO(savedComment);
}


    public CommentDTO editComment(UserDTO userDTO, Long commentId, CommentInputDTO commentInputDTO, PostDTO postDTO) {
    if (commentInputDTO.text() == null || commentInputDTO.text().trim().isEmpty()) {
        throw new IllegalArgumentException("Comment text cannot be empty or null.");
    }
    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));
    User user = userRepository.findById(userDTO.id())
            .orElseThrow(() -> new RuntimeException("User not found"));
    Post post = postRepository.findById(postDTO.id())
            .orElseThrow(() -> new RuntimeException("Post not found"));

    boolean isAdmin = user.getRoles().contains("ADMIN");
    boolean isAuthor = comment.getUser().getId().equals(user.getId());
    boolean isCommentOfPost = comment.getPost().getId().equals(post.getId());

    if ((!isAdmin && !isAuthor) || !isCommentOfPost) {
        throw new RuntimeException("You do not have permission to edit this comment or the comment is not associated with the post");
    }
    comment.setText(Jsoup.clean(commentInputDTO.text(), Safelist.basic()));
    Comment updatedComment = commentRepository.save(comment);
    return commentMapper.toDTO(updatedComment);
}

    @Transactional
public void deleteComment(UserDTO userDTO, Long commentId, PostDTO postDTO) {
    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
    User user = userRepository.findById(userDTO.id())
            .orElseThrow(() -> new RuntimeException("User not found"));
    Post post = postRepository.findById(postDTO.id())
            .orElseThrow(() -> new RuntimeException("Post not found"));

    boolean isAdmin = user.getRoles().contains("ADMIN");
    boolean isAuthor = comment.getUser().getId().equals(user.getId());
    boolean isCommentOfPost = comment.getPost().getId().equals(post.getId());

    if ((!isAdmin && !isAuthor) || !isCommentOfPost) {
        throw new RuntimeException("You do not have permission to edit this comment or the comment is not associated with the post");
    }

    user.removeComment(comment);
    post.removeComment(comment);
    commentRepository.delete(comment);
}

    public CommentRestDTO toRest(CommentDTO commentDTO){
        return commentMapper.toRestDTO(commentDTO);
    }
    public List<CommentRestDTO> toRest(List<CommentDTO> commentDTO){
        return commentMapper.toRestDTOs(commentDTO);
    }
    private byte[] resizeImage(byte[] imageBytes, int maxWidth, int maxHeight) throws IOException {
        // Read the image from byte array
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bais);

        if (originalImage == null) {
            throw new IOException("Could not read image data.");
        }

        // Calculate new dimensions while maintaining aspect ratio
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double scale = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        // Create a new BufferedImage with the new dimensions
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // Enable better image quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        // Write the resized image to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = "JPEG"; // Adjust based on your needs (JPEG, PNG, etc.)
        ImageIO.write(resizedImage, formatName, baos);
        return baos.toByteArray();
    }

}
