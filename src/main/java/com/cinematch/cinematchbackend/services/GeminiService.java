package com.cinematch.cinematchbackend.services;

import com.cinematch.cinematchbackend.model.QuizQuestion;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiService {

    private static final String API_KEY = "AIzaSyCj2QZUqUhGNguDxTOVCUnLC7p5-q7VCqI";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;


    public List<QuizQuestion> getMovieTriviaBatch() {
        try {
            System.out.println("--- FETCHING 5 QUESTIONS FROM GEMINI ---");

            // Ζητάμε 5 ερωτήσεις σε JSON Array
            String prompt = "Generate 5 unique and tough movie trivia questions in greek. " +
                    "Return ONLY a JSON ARRAY (list) where each object has EXACTLY this format: " +
                    "{ \"question\": \"The question text\", \"options\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"], \"correctAnswer\": \"The exact string of the correct option\" } " +
                    "IMPORTANT: The key for the answer MUST be 'correctAnswer' (camelCase). Do NOT use 'correct_answer' or 'answer'. " +
                    "Do not use Markdown formatting. Just the raw JSON array [ ... ].";

            JsonObject part = new JsonObject();
            part.addProperty("text", prompt);

            JsonArray parts = new JsonArray();
            parts.add(part);

            JsonObject content = new JsonObject();
            content.add("parts", parts);

            JsonArray contents = new JsonArray();
            contents.add(content);

            JsonObject root = new JsonObject();
            root.add("contents", contents);

            String requestBody = new Gson().toJson(root);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println(" ERROR: " + response.body());
                return new ArrayList<>();
            }

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

            if (jsonResponse.has("candidates")) {
                String text = jsonResponse.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();


                text = text.replace("```json", "").replace("```", "").trim();
                System.out.println("Questions JSON: " + text);


                Type listType = new TypeToken<List<QuizQuestion>>(){}.getType();
                return gson.fromJson(text, listType);
            }
            return new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}