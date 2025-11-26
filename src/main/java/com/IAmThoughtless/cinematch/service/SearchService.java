package com.IAmThoughtless.cinematch.service;

import com.IAmThoughtless.cinematch.dto.SuggestionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class SearchService {

    // ΕΛΕΓΞΤΕ ΑΥΤΗ ΤΗ ΔΙΕΥΘΥΝΣΗ ΩΣΤΕ ΝΑ ΔΕΙΧΝΕΙ ΣΤΟ SPRING BOOT BACKEND
    private static final String BASE_URL = "http://localhost:8080/api/search/suggestions";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<SuggestionDTO> fetchSuggestionsFromApi(String query) {
        String fullUrl = BASE_URL + "?q=" + query.trim().replace(" ", "%20");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<SuggestionDTO>>() {}
                );
            } else {
                System.err.println("API Status Error: " + response.statusCode());
                return Collections.emptyList();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching suggestions: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
