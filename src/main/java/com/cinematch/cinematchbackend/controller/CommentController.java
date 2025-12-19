package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.services.CommentService;
import org.springframework.web.bind.annotation.*;
import com.cinematch.cinematchbackend.model.Comments_Reviews.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/movie/{movieId}")
    public List<Comment> getCommentsByMovie(@PathVariable Long movieId) {
        List<Comment> comments = commentService.findByMovieId(movieId);
        return comments;
    }

    @PostMapping("/add")
    public Comment addComment(
            @RequestParam("movieId") Long movieId,
            @RequestParam("userName") String userName,
            @RequestParam("text") String text,
            @RequestParam("rating") int rating,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        
        Comment comment = new Comment();
        comment.setMovieId(movieId);
        comment.setUserName(userName);
        comment.setText(text);
        comment.setRating(rating);
        comment.setDateCreated(LocalDateTime.now());
        
        if (image != null && !image.isEmpty()) {
            comment.setImage(image.getBytes());
        }
        
        return commentService.saveComment(comment);
    }
}
