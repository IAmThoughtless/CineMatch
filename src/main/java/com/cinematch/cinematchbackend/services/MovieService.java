package com.cinematch.cinematchbackend.services;

import com.cinematch.cinematchbackend.model.Movie;
import com.cinematch.cinematchbackend.model.MovieResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class MovieService {

    private final String tmdbApiKey;

    public MovieService(@Value("${tmdb.api.key}") String tmdbApiKey) {
        this.tmdbApiKey = tmdbApiKey;
    }

    public MovieResponse fetchTopMovies() {

        String url = "https://api.themoviedb.org/3/movie/popular?api_key=" + tmdbApiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();
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

            HttpClient client = HttpClient.newHttpClient();
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

        } catch (Exception e) {
            System.err.println("Error during search: " + e.getMessage());
            return null;
        }
    }

    public Movie getMovieDetails(Long movieId) {
        try {
            String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + tmdbApiKey + "&append_to_response=reviews";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Search API Request failed: " + response.statusCode());
                return null;
            }

            Gson gson = new Gson();
            return gson.fromJson(response.body(), Movie.class);

        } catch (Exception e) {
            System.err.println("Error during search: " + e.getMessage());
            return null;
        }
    }

    public MovieResponse fetchWhatsHot() {
        String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + tmdbApiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();
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

        try {
            HttpClient client = HttpClient.newHttpClient();
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
}
