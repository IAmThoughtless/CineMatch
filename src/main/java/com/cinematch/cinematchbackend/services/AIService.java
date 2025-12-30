package com.cinematch.cinematchbackend.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;



@Service
public class AIService {
    @Value("${huggingface.api.key}")
    private String apiToken;

    private static final String API_URL = "https://router.huggingface.co/hf-inference/models/dima806/celebs_face_image_detection";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<?> analyzeImage(MultipartFile file) throws IOException {
        // 1. Setup Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        // Wait for model to load if it's cold (can take up to 20 seconds on first request)
        headers.set("x-wait-for-model", "true");

        // 2. Create the Request Entity with image bytes
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        // 3. Send POST request
        // We use ParameterizedTypeReference because the response is a List<CelebrityMatch>
        ResponseEntity<List<?>> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<?>>() {}
        );

        return response.getBody();
    }

}