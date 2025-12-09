package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.UserReview;
import com.cinematch.cinematchbackend.services.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserReview> saveReview(@RequestBody UserReview review) {
        return ResponseEntity.ok(userReviewService.saveReview(review));
    }

    @GetMapping("/{userId}/{tmdbId}")
    public ResponseEntity<UserReview> getUserReviewForMovie(@PathVariable Long userId, @PathVariable Long tmdbId) {
        return ResponseEntity.ok(userReviewService.getUserReviewForMovie(userId, tmdbId));
    }
}