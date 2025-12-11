package com.cinematch.cinematchbackend.services;
import com.cinematch.cinematchbackend.model.Comment;
import com.cinematch.cinematchbackend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // <-- ΝΕΟ IMPORT
import java.util.List;



@Service // 1. Δηλώνουμε την κλάση ως Service Component
public class CommentService {

    private final CommentRepository commentRepository;

    // 2. Ενσωμάτωση του Repository (Dependency Injection)
    // Χρησιμοποιούμε Constructor Injection (καλύτερη πρακτική από το @Autowired πάνω στο πεδίο)
    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }


    /**
     * Επιστρέφει όλα τα σχόλια για μια συγκεκριμένη ταινία, καλώντας το Repository.
     * Αυτή η μέθοδος λείπει από τον Controller σας!
     */
    public List<Comment> findByMovieId(Long movieId) {
        // 3. Καλούμε τη μέθοδο του Repository που μιλάει στη βάση
        return commentRepository.findByMovieId(movieId);
    }
}