package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.Movie.MovieResponse;
import com.cinematch.cinematchbackend.model.Star.UserStar;
import com.cinematch.cinematchbackend.model.Star.UserStarDTO;
import com.cinematch.cinematchbackend.services.UserStarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stars")
@CrossOrigin(origins = "http://localhost:3000")
public class UserStarController {

    @Autowired
    private UserStarService userStarService;

    @GetMapping("/{userId}")
    public ResponseEntity<MovieResponse> getAllUserStars(@PathVariable Long userId) {
        return ResponseEntity.ok(userStarService.getAllUserStars(userId));
    }

    @PostMapping
    public ResponseEntity<UserStarDTO> addUserStar(@RequestBody UserStar userStar) {
        return ResponseEntity.ok(userStarService.addUserStar(userStar));
    }

    @DeleteMapping("/{userId}/{tmdbId}")
    public ResponseEntity<Void> unstarMovie(@PathVariable Long userId, @PathVariable Long tmdbId) {
        userStarService.unstarMovie(userId, tmdbId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/{tmdbId}")
    public ResponseEntity<Boolean> isMovieStarred(@PathVariable Long userId, @PathVariable Long tmdbId) {
        return ResponseEntity.ok(userStarService.isMovieStarred(userId, tmdbId));
    }
}
