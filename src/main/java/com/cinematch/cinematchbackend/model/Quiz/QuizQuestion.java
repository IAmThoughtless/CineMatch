package com.cinematch.cinematchbackend.model.Quiz;

import com.google.gson.annotations.SerializedName; // <--- Σημαντικό Import
import java.util.List;

public class QuizQuestion {

    public String question;

    public List<String> options;

    // Gson is looking for any of these in the JSON
    @SerializedName(value = "correctAnswer", alternate = {"correct_answer", "answer", "CorrectAnswer", "correct"})
    public String correctAnswer;

    @Override
    public String toString() {
        return "Q: " + question + " | Ans: " + correctAnswer;
    }
}