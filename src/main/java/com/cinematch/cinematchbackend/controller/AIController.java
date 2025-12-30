package com.cinematch.cinematchbackend.controller;


import com.cinematch.cinematchbackend.model.Comments_Reviews.UserReview;
import com.cinematch.cinematchbackend.model.User;
import com.cinematch.cinematchbackend.services.AIService;
import com.cinematch.cinematchbackend.services.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    @Autowired
    private AIService aiService;

    @PostMapping("/actor-similarity")
    public ResponseEntity<?> imageSimilarity(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        try {
            List<?> matches = aiService.analyzeImage(image);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            // This handles the "Model Loading" error or bad keys
            return ResponseEntity.status(500).body("Error processing image: " + e.getMessage());
        }
    }
}