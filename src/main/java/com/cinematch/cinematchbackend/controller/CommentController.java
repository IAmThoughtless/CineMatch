package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.repository.CommentRepository;
import com.cinematch.cinematchbackend.services.CommentService;
import com.cinematch.cinematchbackend.services.MovieService;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.cinematch.cinematchbackend.model.Comment;
import org.springframework.beans.factory.annotation.Autowired; // Autowired
import org.springframework.web.bind.annotation.RestController; //  Για το RestController
import org.springframework.web.bind.annotation.RequestMapping; //  Για το RequestMapping
import org.springframework.web.bind.annotation.GetMapping;     //  Για το GetMapping
import org.springframework.web.bind.annotation.PathVariable;  //  Για το PathVariable
import org.springframework.web.bind.annotation.CrossOrigin;   //  Για το CrossOrigin

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:3000") // Ή βάλτε το domain που φιλοξενεί τη σελίδα σας
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/movie/{movieId}")
    public List<Comment> getCommentsByMovie(@PathVariable Long movieId) {
        List<Comment> comments = commentService.findByMovieId(movieId);
        return comments;
    }
}


