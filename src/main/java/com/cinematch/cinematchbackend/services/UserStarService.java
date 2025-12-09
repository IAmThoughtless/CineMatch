package com.cinematch.cinematchbackend.services;

import com.cinematch.cinematchbackend.model.Movie;
import com.cinematch.cinematchbackend.model.MovieResponse;
import com.cinematch.cinematchbackend.model.UserStar;
import com.cinematch.cinematchbackend.model.UserStarDTO;
import com.cinematch.cinematchbackend.repository.UserStarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserStarService {

    @Autowired
    private UserStarRepository userStarRepository;

    @Autowired
    private MovieService movieService;

    public MovieResponse getAllUserStars(Long userId) {
        List<UserStar> userStars = userStarRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<Movie> movies = userStars.stream()
                .map(star -> movieService.getMovieDetails(star.getTmdbId()))
                .collect(Collectors.toList());

        MovieResponse movieResponse = new MovieResponse();
        movieResponse.setResults(movies);
        return movieResponse;
    }

    public UserStarDTO addUserStar(UserStar userStar) {
        userStar.setCreatedAt(new Date());
        UserStar savedStar = userStarRepository.save(userStar);
        return new UserStarDTO(savedStar);
    }

    @Transactional
    public void unstarMovie(Long userId, Long tmdbId) {
        userStarRepository.deleteByUserIdAndTmdbId(userId, tmdbId);
    }

    public boolean isMovieStarred(Long userId, Long tmdbId) {
        return userStarRepository.existsByUserIdAndTmdbId(userId, tmdbId);
    }
}
