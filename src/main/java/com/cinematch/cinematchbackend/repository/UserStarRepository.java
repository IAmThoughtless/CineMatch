package com.cinematch.cinematchbackend.repository;

import com.cinematch.cinematchbackend.model.UserStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStarRepository extends JpaRepository<UserStar, Long> {
    List<UserStar> findByUserId(Long userId);
    void deleteByUserIdAndTmdbId(Long userId, Long tmdbId);
    boolean existsByUserIdAndTmdbId(Long userId, Long tmdbId);
}
