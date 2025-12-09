package com.cinematch.cinematchbackend.repository;

import com.cinematch.cinematchbackend.model.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Long> {
    List<UserReview> findByTmdbId(Long tmdbId);
    UserReview findByUserIdAndTmdbId(Long userId, Long tmdbId);
}
