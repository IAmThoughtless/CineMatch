package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.services.CommentService;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.cinematch.cinematchbackend.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
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
}


