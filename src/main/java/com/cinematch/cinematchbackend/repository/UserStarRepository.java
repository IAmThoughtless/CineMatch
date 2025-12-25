package com.cinematch.cinematchbackend.repository;

import com.cinematch.cinematchbackend.model.Star.UserStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStarRepository extends JpaRepository<UserStar, Long> {
    List<UserStar> findByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByUserIdAndTmdbId(Long userId, Long tmdbId);
    boolean existsByUserIdAndTmdbId(Long userId, Long tmdbId);
}
