package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.QuizQuestion;
import com.cinematch.cinematchbackend.services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private GeminiService geminiService;

    @CrossOrigin(origins = "*")
    @GetMapping("/batch")
    public ResponseEntity<List<QuizQuestion>> getQuizBatch() {
        List<QuizQuestion> questions = geminiService.getMovieTriviaBatch();
        if (questions != null && !questions.isEmpty()) {
            return ResponseEntity.ok(questions);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}