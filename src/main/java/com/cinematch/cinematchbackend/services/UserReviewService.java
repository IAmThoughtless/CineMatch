package com.cinematch.cinematchbackend.services;

import com.cinematch.cinematchbackend.model.UserReview;
import com.cinematch.cinematchbackend.repository.UserReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date; // Import Date

import java.util.List;

@Service
public class UserReviewService {

    @Autowired
    private UserReviewRepository userReviewRepository;

    public List<UserReview> getReviewsByTmdbId(Long tmdbId) {
        return userReviewRepository.findByTmdbId(tmdbId);
    }

    @Transactional
    public UserReview saveReview(UserReview reviewFromRequest) {
        // Check if a review already exists for this user and movie
        UserReview existingReview = userReviewRepository.findByUserIdAndTmdbId(
                reviewFromRequest.getUser().getId(),
                reviewFromRequest.getTmdbId()
        );

        if (existingReview != null) {
            // If it exists, just update the text
            existingReview.setReviewText(reviewFromRequest.getReviewText());
            // Do NOT update createdAt for existing reviews
            return userReviewRepository.save(existingReview);
        } else {
            // If it's a new review, ensure ID is null to let the DB handle it
            reviewFromRequest.setId(null);
            // Explicitly set createdAt for new reviews
            reviewFromRequest.setCreatedAt(new Date()); 
            return userReviewRepository.save(reviewFromRequest);
        }
    }

    public UserReview getUserReviewForMovie(Long userId, Long tmdbId) {
        return userReviewRepository.findByUserIdAndTmdbId(userId, tmdbId);
    }
}
