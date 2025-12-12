package com.cinematch.cinematchbackend.services;
import com.cinematch.cinematchbackend.model.Comment;
import com.cinematch.cinematchbackend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // <-- ΝΕΟ IMPORT
import java.util.List;



@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }


    public List<Comment> findByMovieId(Long movieId) {
        return commentRepository.findByMovieId(movieId);
    }
}