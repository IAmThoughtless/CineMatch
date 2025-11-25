package com.example.cinematch;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MovieService {

    // NOTE: Keep the API key here, but consider reading it from an environment variable!
    private final String TMDB_API_KEY = "71890bc04b3c153a8abf55ea6cdfbe46";

    /**
     * Fetches a list of popular movies from the TMDB API.
     * @return MovieResponse object or null if the API call fails.
     */
    public MovieResponse fetchTopMovies() {
        String url = "https://api.themoviedb.org/3/movie/popular?api_key=" + TMDB_API_KEY;

        try {
            // HttpClient client = HttpClient.newHttpClient(); // Use try-with-resources for client if you were making multiple calls, but this is fine.
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            // The client.send() is a blocking call, which is why it runs on a background thread (CompletableFuture)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("API Request failed with status: " + response.statusCode());
                return null;
            }

            Gson gson = new Gson();
            // Since MovieResponse and Movie are in the same package, Gson can access them.
            return gson.fromJson(response.body(), MovieResponse.class);

        } catch (Exception e) {
            System.err.println("Error during API call in MovieService: " + e.getMessage());
            return null;
        }
    }
}