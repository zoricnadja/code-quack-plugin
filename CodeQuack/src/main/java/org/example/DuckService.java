package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import org.example.model.ChatRequest;
import org.example.model.ChatResponse;
import org.example.model.Message;

public class DuckService {
    private static final String BACKEND_URL = "http://localhost:8080/api/chats/messages";

    private final HttpClient httpClient;
    private final Gson gson;

    private final List<Message> conversationHistory = new ArrayList<>();

    public DuckService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public CompletableFuture<String> askTheDuck(String codeContext, String userQuestion) {
        ChatRequest requestPayload = new ChatRequest(conversationHistory , codeContext, userQuestion);
        String jsonBody = gson.toJson(requestPayload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        ChatResponse chatResponse = gson.fromJson(response.body(), ChatResponse.class);
                        String aiAnswer = chatResponse.getResponse();
                        updateHistory(codeContext, userQuestion, aiAnswer);
                        return aiAnswer;
                    } else {
                        return "Error: Backend returned status " + response.statusCode();
                    }
                })
                .exceptionally(ex -> "Error connecting to backend: " + ex.getMessage());
    }

    private synchronized void updateHistory(String code, String question, String answer) {
        String fullUserMsg = "Code Context:\n" + code + "\n\nUser Question: " + question;

        conversationHistory.add(new Message("user", fullUserMsg));
        conversationHistory.add(new Message("assistant", answer));
    }

}
