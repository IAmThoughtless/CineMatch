package com.cinematch.cinematchbackend.controller;


import com.cinematch.cinematchbackend.model.Movie.Movie;
import com.cinematch.cinematchbackend.services.AIService;
import com.cinematch.cinematchbackend.services.MovieService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    @Autowired
    private AIService aiService;

    @Autowired
    private MovieService movieService;

    @PostMapping("/actor-similarity")
    public ResponseEntity<String> imageSimilarity(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        try {
            String result = aiService.analyzeImage(image.getBytes());
            
            // Parse the result to get the actor name
            // Assuming result format is "Name: [Actor Name], Similarity: [Percentage]" or similar
            // We might need to adjust parsing based on actual AI response.
            // For now, let's assume the AI returns the name clearly or we can extract it.
            // But wait, the prompt says "Return ONLY the name and the similarity percentage."
            // Example: "Brad Pitt 85%" or "Brad Pitt, 85%"
            
            // Let's try to extract the name. 
            // A simple heuristic: take the string before the first number or comma.
            String actorName = result.replaceAll("[0-9%].*", "").trim();
            if (actorName.endsWith(",")) {
                actorName = actorName.substring(0, actorName.length() - 1).trim();
            }
            
            // Search for the person in TMDB
            Movie.CastMember person = movieService.searchPerson(actorName);
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("ai_response", result);
            if (person != null) {
                responseMap.put("actor_details", person);
            }
            
            Gson gson = new Gson();
            return ResponseEntity.ok(gson.toJson(responseMap));
            
        } catch (Exception e) {
            // This handles the "Model Loading" error or bad keys
            return ResponseEntity.status(500).body("Error processing image: " + e.getMessage());
        }
    }
}