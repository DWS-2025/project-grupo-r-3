package com.example.unitalk.controllers;


import com.example.unitalk.models.*;
import com.example.unitalk.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

@Controller
@RequestMapping("/subjects/{id1}/posts/{id2}")
public class CommentController {
    @Autowired
    private SubjectService subjects;
    @Autowired
    private PostService posts;
    @Autowired
    private UserService users;
    @Autowired
    private CommentService comments;
    @GetMapping
    public String showPostComments(@PathVariable("id1") int idSubject, @PathVariable("id2") int idPost, Model model){
        User user = users.getUser();
        Subject subject = subjects.findById(idSubject);
        Post post = posts.findById(idPost);
        if(user.getSubjects().contains(subject)){
            model.addAttribute("comments",post.getComments());
            model.addAttribute("post",post);
            model.addAttribute("subject",subject);
        }
        else{
            model.addAttribute("error","You have not applied to this subject");
        }
        return "post";
    }
    @PostMapping("/comment")
    public String addComment(@PathVariable("id1") int idSubject, @PathVariable("id2") int idPost, @RequestParam("commentText") String commentText, @RequestParam(value = "image", required = false) MultipartFile image) throws IOException{
        User user = users.getUser();
        Post post = posts.findById(idPost);
        comments.createComment(user, commentText, post, image);
            return "redirect:/subjects/{id1}/posts/{id2}";
    }
    @PostMapping("/edit-comment")
    public String editComment(@PathVariable("id1") int idSubject, @PathVariable("id2") int idPost,@RequestParam("commentId") int commentId, @RequestParam("commentText") String commentText){
        Comment comment = comments.findById(commentId);
        if(comment!=null){
            comment.setText(commentText);
        }
        return "redirect:/subjects/{id1}/posts/{id2}";
    }
    @PostMapping("/delete-comment")
    public String deleteComment(@PathVariable("id1") int idSubject, @PathVariable("id2") int idPost,@RequestParam("commentId") int commentId){
        User user = users.getUser();
        Post post = posts.findById(idPost);
        Comment comment = comments.findById(commentId);
        if (comment != null) {
            comments.deleteComment(user, comment, post);
        }
        return "redirect:/subjects/{id1}/posts/{id2}";
    }
    @GetMapping("/comment-image/{imageName}")
    public ResponseEntity<Resource> getCommentImage(@PathVariable("id1") int idSubject, @PathVariable("id2") int idPost, @PathVariable String imageName) throws IOException {
        Path imagePath = Paths.get("uploads/comments").resolve(imageName);

        if (Files.exists(imagePath)) {
            Resource imageResource = new UrlResource(imagePath.toUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(imageResource);
        }

        return ResponseEntity.notFound().build();
    }

}
