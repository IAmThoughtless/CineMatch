package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.Movie;
import com.cinematch.cinematchbackend.model.MovieResponse;
import com.cinematch.cinematchbackend.model.MovieWithReviews;
import com.cinematch.cinematchbackend.model.UserReview;
import com.cinematch.cinematchbackend.services.UserReviewService;
import com.cinematch.cinematchbackend.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private UserReviewService userReviewService;

    @CrossOrigin(origins = "*")
    @GetMapping("/whats-hot")
    public ResponseEntity<MovieResponse> getWhatsHotMovies() {
        MovieResponse ds = movieService.fetchWhatsHot();
        return ResponseEntity.ok(ds);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public ResponseEntity<MovieWithReviews> getMovie(@PathVariable Long id) {
        Movie movie = movieService.getMovieDetails(id);
        List<UserReview> reviews = userReviewService.getReviewsByTmdbId(id);
        MovieWithReviews response = new MovieWithReviews(movie, reviews);
        return ResponseEntity.ok(response);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/search")
    public ResponseEntity<MovieResponse> searchMovie(@RequestBody String query) {
        MovieResponse ds = movieService.searchMovies(query);
        return ResponseEntity.ok(ds);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/top-10")
    public ResponseEntity<MovieResponse> searchMovie() {
        MovieResponse ds = movieService.fetchTopMovies();
        return ResponseEntity.ok(ds);
    }
}
