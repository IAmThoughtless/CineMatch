package com.cinematch.cinematchbackend.controller;

import com.cinematch.cinematchbackend.model.LeaderboardDTO;
import com.cinematch.cinematchbackend.model.Quiz;
import com.cinematch.cinematchbackend.model.QuizQuestion;
import com.cinematch.cinematchbackend.model.QuizSubmission;
import com.cinematch.cinematchbackend.model.User;
import com.cinematch.cinematchbackend.services.GeminiService;
import com.cinematch.cinematchbackend.services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.cinematch.cinematchbackend.model.UserStar;
import com.cinematch.cinematchbackend.repository.UserStarRepository;
import java.util.stream.Collectors;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private UserStarRepository userStarRepository;

    @Autowired
    private QuizService quizService;

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
                .limit(10)
                .collect(Collectors.toList());

        List<QuizQuestion> questions = geminiService.getPersonalizedTrivia(titles);

        if (questions != null && !questions.isEmpty()) {
            return ResponseEntity.ok(questions);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/submit")
    public ResponseEntity<Quiz> submitQuiz(@RequestBody QuizSubmission submission) {
        Quiz quiz = new Quiz();
        User user = new User();
        user.setId(submission.getUserId());
        quiz.setUser(user);
        quiz.setScore(submission.getScore());
        quiz.setMaxScore(submission.getMaxScore());
        quiz.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return ResponseEntity.ok(quizService.saveQuiz(quiz));
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardDTO>> getLeaderboard() {
        return ResponseEntity.ok(quizService.getLeaderboard());
    }
}
