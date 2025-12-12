package com.cinematch.cinematchbackend.services;

import com.cinematch.cinematchbackend.model.LeaderboardDTO;
import com.cinematch.cinematchbackend.model.Quiz;
import com.cinematch.cinematchbackend.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public List<LeaderboardDTO> getLeaderboard() {
        List<Object[]> results = quizRepository.findLeaderboard();
        return results.stream()
                .map(result -> {
                    Timestamp createdAt = (Timestamp) result[3];
                    String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(createdAt);
                    return new LeaderboardDTO((Long) result[0], (String) result[1], (Integer) result[2], formattedDate);
                })
                .collect(Collectors.toList());
    }
}
