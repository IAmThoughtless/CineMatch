package com.cinematch.cinematchbackend.repository;
import com.cinematch.cinematchbackend.model.Comment;
import com.cinematch.cinematchbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByMovieId(Long movieId);
}


