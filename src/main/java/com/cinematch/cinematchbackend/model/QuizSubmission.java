package com.cinematch.cinematchbackend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSubmission {
    private Long userId;
    private int score;
    private int maxScore;
}
