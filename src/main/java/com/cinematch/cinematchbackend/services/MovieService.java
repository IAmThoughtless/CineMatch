package com.cinematch.cinematchbackend.services;

import com.cinematch.cinematchbackend.model.MovieResponse;
import com.google.gson.Gson;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class MovieService {


    private final String TMDB_API_KEY = "71890bc04b3c153a8abf55ea6cdfbe46";

    /**
     * Fetches a list of popular movies from the TMDB API.
     * @return MovieResponse object or null if the API call fails.
     */
    public MovieResponse fetchTopMovies() {
        String url = "https://api.themoviedb.org/3/movie/popular?api_key=" + TMDB_API_KEY;

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

    /**
     * Searches for movies based on a query string.
     * @param query The movie title to search for.
     * @return MovieResponse object or null if the API call fails.
     */
    public MovieResponse searchMovies(String query) {
        try {

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://api.themoviedb.org/3/search/movie?api_key=" + TMDB_API_KEY + "&query=" + encodedQuery;

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

    /**
     * Gets the details for a specific movie based on a movie id.
     * @param movieId The movie id to search for.
     * @return MovieResponse object or null if the API call fails.
     */
    public MovieResponse getMovie(String movieId) {
        try {
            String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + TMDB_API_KEY;

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
}