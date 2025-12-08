package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.MovieResponse;
import com.cinematch.cinematchbackend.repository.UserRepository;
import com.cinematch.cinematchbackend.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MovieService movieService;

    @CrossOrigin(origins = "*")
    @GetMapping("/whats-hot")
    public ResponseEntity<MovieResponse> getWhatsHotMovies() {
        MovieResponse ds = movieService.fetchWhatsHot();
        return ResponseEntity.ok(ds);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable String id) {
        MovieResponse ds = movieService.getMovie(id);
        return ResponseEntity.ok(ds);
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