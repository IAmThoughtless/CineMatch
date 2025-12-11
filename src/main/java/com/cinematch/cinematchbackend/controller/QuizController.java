package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.QuizQuestion;
import com.cinematch.cinematchbackend.services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.cinematch.cinematchbackend.model.UserStar;
import com.cinematch.cinematchbackend.repository.UserStarRepository;
import java.util.stream.Collectors;



    @RestController
    @RequestMapping("/api/quiz")
    public class QuizController {

        @Autowired
        private GeminiService geminiService;


        @Autowired
        private UserStarRepository userStarRepository;

        @CrossOrigin(origins = "*")
        @GetMapping("/batch")
        public ResponseEntity<List<QuizQuestion>> getQuizBatch() {
            return ResponseEntity.ok(geminiService.getMovieTriviaBatch());
        }


        @CrossOrigin(origins = "*")
        @GetMapping("/personalized/{userId}")
        public ResponseEntity<?> getPersonalizedQuiz(@PathVariable Long userId) {

            List<UserStar> stars = userStarRepository.findByUserIdOrderByCreatedAtDesc(userId);


            if (stars.isEmpty() || stars.size() < 3) {
                return ResponseEntity.badRequest().body("Not enough starred movies. Please star at least 3 movies!");
            }


            List<String> titles = stars.stream()
                    .map(UserStar::getTitle)
                    .limit(10) // Στέλνουμε max 10 ταινίες για να μην μπερδέψουμε το AI
                    .collect(Collectors.toList());


            List<QuizQuestion> questions = geminiService.getPersonalizedTrivia(titles);

            if (questions != null && !questions.isEmpty()) {
                return ResponseEntity.ok(questions);
            } else {
                return ResponseEntity.internalServerError().build();
            }
        }
    }
