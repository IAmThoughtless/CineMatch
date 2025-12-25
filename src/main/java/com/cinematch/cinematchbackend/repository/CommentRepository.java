package com.cinematch.cinematchbackend.repository;
import com.cinematch.cinematchbackend.model.Comments_Reviews.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByMovieId(Long movieId);
}


