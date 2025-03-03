package com.example.unitalk.controllers;


import com.example.unitalk.models.*;
import com.example.unitalk.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String addComment(@PathVariable("id1") int idSubject, @PathVariable("id2") int idPost, @RequestParam("commentText") String commentText){
        User user = users.getUser();
        Post post = posts.findById(idPost);
        comments.createComment(user, commentText, post);
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
}
