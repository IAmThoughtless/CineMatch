package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.Movie.Movie;
import com.cinematch.cinematchbackend.model.Movie.MovieResponse;
import com.cinematch.cinematchbackend.model.Movie.MovieWithReviews;
import com.cinematch.cinematchbackend.model.Comments_Reviews.UserReview;
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

    @CrossOrigin(origins = "*")
    @GetMapping("/genre/{genreId}")
    public ResponseEntity<MovieResponse> getMoviesByGenre(@PathVariable int genreId) {
        MovieResponse ds = movieService.fetchByGenre(genreId);
        return ResponseEntity.ok(ds);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/suggestions/{userId}")
    public ResponseEntity<MovieResponse> getSuggestions(@PathVariable Long userId) {
        MovieResponse ds = movieService.getSuggestions(userId);
        return ResponseEntity.ok(ds);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/actor/{actorId}")
    public ResponseEntity<MovieResponse> getMoviesByActor(@PathVariable int actorId) {
        MovieResponse ds = movieService.fetchMoviesByActor(actorId);
        return ResponseEntity.ok(ds);
    }
}
