package com.cinematch.cinematchbackend.services;

import com.cinematch.cinematchbackend.model.Movie.Movie;
import com.cinematch.cinematchbackend.model.Movie.MovieResponse;
import com.cinematch.cinematchbackend.model.Star.UserStarDTO;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class MovieService {

    private final String tmdbApiKey;
    private final UserStarService userStarService;

    @Autowired
    public MovieService(@Value("${tmdb.api.key}") String tmdbApiKey, @Lazy UserStarService userStarService) {
        this.tmdbApiKey = tmdbApiKey;
        this.userStarService = userStarService;
    }

    public MovieResponse fetchTopMovies() {

        String url = "https://api.themoviedb.org/3/movie/popular?api_key=" + tmdbApiKey;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("API Request failed with status: " + response.statusCode());
                return null;
            }

            Gson gson = new Gson();
            return gson.fromJson(response.body(), MovieResponse.class);

        } catch (Exception e) {
            System.err.println("Error during API call in MovieService: " + e.getMessage());
            return null;
        }
    }

    public MovieResponse searchMovies(String query) {
        try {

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://api.themoviedb.org/3/search/movie?api_key=" + tmdbApiKey + "&query=" + encodedQuery;

            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    System.err.println("Search API Request failed: " + response.statusCode());
                    return null;
                }

                Gson gson = new Gson();
                return gson.fromJson(response.body(), MovieResponse.class);
            }

        } catch (Exception e) {
            System.err.println("Error during search: " + e.getMessage());
            return null;
        }
    }

    public Movie getMovieDetails(Long movieId) {
        Movie movie = null;
        try {

            String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + tmdbApiKey + "&append_to_response=reviews,credits,videos";

            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    System.err.println("Movie Details API Request failed: " + response.statusCode());
                    return null;
                }

                Gson gson = new Gson();
                movie = gson.fromJson(response.body(), Movie.class);

                com.google.gson.JsonObject jsonObject = gson.fromJson(response.body(), com.google.gson.JsonObject.class);

                // 2. Mapping για το Cast (Credits)
                if (jsonObject.has("credits")) {
                    com.google.gson.JsonObject credits = jsonObject.getAsJsonObject("credits");
                    if (credits.has("cast")) {
                        java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<Movie.CastMember>>(){}.getType();
                        List<Movie.CastMember> castList = gson.fromJson(credits.get("cast"), listType);
                        movie.setCast(castList);
                    }
                }

                // 3. Mapping για το Trailer (Videos)
                if (jsonObject.has("videos")) {
                    com.google.gson.JsonObject videosObj = jsonObject.getAsJsonObject("videos");
                    if (videosObj.has("results")) {
                        com.google.gson.JsonArray results = videosObj.getAsJsonArray("results");

                        boolean trailerFound = false;
                        for (com.google.gson.JsonElement el : results) {
                            com.google.gson.JsonObject video = el.getAsJsonObject();

                            // Ελέγχουμε αν είναι YouTube και αν ο τύπος είναι Trailer
                            String site = video.has("site") ? video.get("site").getAsString() : "";
                            String type = video.has("type") ? video.get("type").getAsString() : "";

                            if (site.equalsIgnoreCase("YouTube") && type.equalsIgnoreCase("Trailer")) {
                                String key = video.get("key").getAsString();
                                movie.setTrailerKey(key);
                                System.out.println("DEBUG BACKEND: Found Trailer Key [" + key + "] for movie: " + movie.getTitle());
                                trailerFound = true;
                                break;
                            }
                        }
                        if (!trailerFound) {
                            System.out.println("DEBUG BACKEND: No official YouTube Trailer found for: " + movie.getTitle());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during getMovieDetails in MovieService: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return movie;
    }

    public MovieResponse fetchWhatsHot() {
        String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + tmdbApiKey;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("What's Hot API Request failed with status: " + response.statusCode());
                return null;
            }

            Gson gson = new Gson();
            return gson.fromJson(response.body(), MovieResponse.class);

        } catch (Exception e) {
            System.err.println("Error during What's Hot API call in MovieService: " + e.getMessage());
            return null;
        }
    }

    public MovieResponse fetchByGenre(int genreId) {
        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + tmdbApiKey + "&with_genres=" + genreId;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Genre API Request failed with status: " + response.statusCode());
                return null;
            }

            Gson gson = new Gson();
            return gson.fromJson(response.body(), MovieResponse.class);

        } catch (Exception e) {
            System.err.println("Error during Genre API call in MovieService: " + e.getMessage());
            return null;
        }
    }

    public MovieResponse getSuggestions(Long userId) {
        List<UserStarDTO> starredMovies = userStarService.getStarsByUserId(userId);
        if (starredMovies.isEmpty()) {
            MovieResponse movieResponse = new MovieResponse();
            movieResponse.setResults(Collections.emptyList());
            return movieResponse;
        }

        UserStarDTO randomStar = starredMovies.get(new Random().nextInt(starredMovies.size()));
        Long movieId = randomStar.getTmdbId();

        String url = "https://api.themoviedb.org/3/movie/" + movieId + "/similar?api_key=" + tmdbApiKey;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Similar Movies API Request failed with status: " + response.statusCode());
                return null;
            }

            Gson gson = new Gson();
            return gson.fromJson(response.body(), MovieResponse.class);

        } catch (Exception e) {
            System.err.println("Error during Similar Movies API call in MovieService: " + e.getMessage());
            return null;
        }
    }

    public MovieResponse fetchMoviesByActor(int actorId) {
        String url = "https://api.themoviedb.org/3/person/" + actorId + "/movie_credits?api_key=" + tmdbApiKey;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Actor Movies API Request failed with status: " + response.statusCode());
                return null;
            }

            Gson gson = new Gson();
            // The response from /person/{id}/movie_credits has a "cast" field which is a list of movies.
            // MovieResponse expects "results".
            // We need to map "cast" to "results" or create a custom deserializer/DTO.
            // However, MovieResponse has "results".
            // Let's check the structure of movie_credits response.
            // It returns { "cast": [ ... ], "crew": [ ... ], "id": ... }
            // MovieResponse has "results".
            // So we can't directly map it to MovieResponse unless we change MovieResponse or use a temporary object.
            
            com.google.gson.JsonObject jsonObject = gson.fromJson(response.body(), com.google.gson.JsonObject.class);
            if (jsonObject.has("cast")) {
                com.google.gson.JsonArray castArray = jsonObject.getAsJsonArray("cast");
                // We can construct a JSON that matches MovieResponse structure: { "results": [ ... ] }
                com.google.gson.JsonObject newJson = new com.google.gson.JsonObject();
                newJson.add("results", castArray);
                return gson.fromJson(newJson, MovieResponse.class);
            }
            
            return new MovieResponse();

        } catch (Exception e) {
            System.err.println("Error during Actor Movies API call in MovieService: " + e.getMessage());
            return null;
        }
    }
}
