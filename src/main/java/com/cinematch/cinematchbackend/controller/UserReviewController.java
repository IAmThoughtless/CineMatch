package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.Comments_Reviews.UserReview;
import com.cinematch.cinematchbackend.model.User;
import com.cinematch.cinematchbackend.services.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class UserReviewController {

    @Autowired
    private UserReviewService userReviewService;

    @GetMapping("/{tmdbId}")
    public ResponseEntity<List<UserReview>> getReviews(@PathVariable Long tmdbId) {
        return ResponseEntity.ok(userReviewService.getReviewsByTmdbId(tmdbId));
    }

    @PostMapping
    public ResponseEntity<UserReview> saveReview(
            @RequestParam("tmdbId") Long tmdbId,
            @RequestParam("reviewText") String reviewText,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        UserReview review = new UserReview();
        review.setTmdbId(tmdbId);
        review.setReviewText(reviewText);
        review.setCreatedAt(new Date());
        
        User user = new User();
        user.setId(userId);
        review.setUser(user);

        if (image != null && !image.isEmpty()) {
            review.setImage(image.getBytes());
        }

        return ResponseEntity.ok(userReviewService.saveReview(review));
    }

    @GetMapping("/{userId}/{tmdbId}")
    public ResponseEntity<UserReview> getUserReviewForMovie(@PathVariable Long userId, @PathVariable Long tmdbId) {
        return ResponseEntity.ok(userReviewService.getUserReviewForMovie(userId, tmdbId));
    }
}