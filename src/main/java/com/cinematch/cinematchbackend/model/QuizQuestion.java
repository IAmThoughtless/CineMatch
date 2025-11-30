package com.cinematch.cinematchbackend.model;

import com.google.gson.annotations.SerializedName; // <--- Σημαντικό Import
import java.util.List;

public class QuizQuestion {

    public String question;

    public List<String> options;

    // Αυτή η γραμμή λέει στο Gson να ψάξει για οποιοδήποτε από αυτά τα ονόματα στο JSON
    @SerializedName(value = "correctAnswer", alternate = {"correct_answer", "answer", "CorrectAnswer", "correct"})
    public String correctAnswer;

    // Προσθέτουμε και toString για να βλέπουμε τι γίνεται στην κονσόλα (Debugging)
    @Override
    public String toString() {
        return "Q: " + question + " | Ans: " + correctAnswer;
    }
}