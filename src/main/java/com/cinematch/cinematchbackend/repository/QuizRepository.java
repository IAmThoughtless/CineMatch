package com.cinematch.cinematchbackend.repository;

import com.cinematch.cinematchbackend.model.Quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query("SELECT q.user.id, q.user.username, MAX(q.score) as max_score, MAX(q.createdAt) as max_created_at FROM Quiz q GROUP BY q.user.id, q.user.username ORDER BY max_score DESC, max_created_at DESC")
    List<Object[]> findLeaderboard();
}
