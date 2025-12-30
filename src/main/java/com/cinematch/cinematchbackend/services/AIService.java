package com.cinematch.cinematchbackend.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.util.List;



@Service
public class AIService {

    private static final String API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String MODEL_ID = "Qwen/Qwen2.5-VL-7B-Instruct";

    private final RestClient restClient;
    public AIService(@Value("${huggingface.api.key}") String apiKey, RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl(API_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String analyzeImage(byte[] imageBytes) throws IOException {
        // 1. Setup Headers
        // 1. Convert Raw Bytes -> Base64 String
        String base64String = Base64.getEncoder().encodeToString(imageBytes);

        // 2. Format as Data URI (Strictly required by the API)
        // Assumes JPEG/PNG. If strictly PNG, change to image/png.
        String dataUri = "data:image/jpeg;base64," + base64String;

        // 3. Construct JSON Payload
        var payload = Map.of(
                "model", MODEL_ID,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of(
                                                "type", "text",
                                                "text", "Who is the celebrity in this image? Return ONLY the name and the similarity percentage."
                                        ),
                                        Map.of(
                                                "type", "image_url",
                                                "image_url", Map.of("url", dataUri) // <-- The Base64 goes here
                                        )
                                )
                        )
                ),
                "max_tokens", 50
        );

        // 4. Send Request
        Map response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(Map.class);

        // 5. Extract Answer
        return extractContent(response);
    }

    private String extractContent(Map response) {
        try {
            List<Map> choices = (List<Map>) response.get("choices");
            if (choices == null || choices.isEmpty()) return "No match found";
            Map message = (Map) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            return "Parsing Error";
        }
    }
}