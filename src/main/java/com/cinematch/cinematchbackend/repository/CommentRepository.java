package com.cinematch.cinematchbackend.repository;
import com.cinematch.cinematchbackend.model.Comment;
import com.cinematch.cinematchbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


// Αυτό το interface κληρονομεί όλες τις βασικές μεθόδους DB (find, save, delete)
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Αυτή είναι η μέθοδος που χρειάζεται ο Controller.
    // Το Spring Data JPA δημιουργεί αυτόματα το SQL query από το όνομα της μεθόδου.
    List<Comment> findByMovieId(Long movieId);
}


