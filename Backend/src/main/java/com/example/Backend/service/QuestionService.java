package com.example.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.backend.model.OpenAiRequest;
import com.example.backend.model.OpenAiRequest.Message;
import com.example.backend.model.OpenAiResponse;


import java.util.Arrays;
import java.util.List;

import java.util.Arrays;
import java.util.List;

@Service
public class QuestionService {
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";
    private static final String COMPLETIONS_URI = "/chat/completions";

    private final WebClient webClient;

    private static final String SYSTEM_PROMPT = """
        You are an expert software engineer acting as a 'Rubber Duck'.
        Your Goal is to help the user debug their code by guiding them to the solution themselves.
        DO NOT provide the solution or fixed code. Only ask one or two probing questions per response.
        """;

    public QuestionService(WebClient openAiWebClient) {
        this.webClient = openAiWebClient;
    }

    public String askQuestion(String codeContext, String userProblem) {

        String userContent = String.format("Code Snippet:\n```java\n%s\n```\n\nMy Problem: %s",
                codeContext, userProblem);

        // 1. KREIRANJE PORUKA BEZ BUILDERA (Korišćenje konstruktora)
        Message systemMessage = new Message("system", SYSTEM_PROMPT);
        Message userMessage = new Message("user", userContent);

        List<Message> messages = Arrays.asList(systemMessage, userMessage);

        // 2. KREIRANJE ZAHTEVA BEZ BUILDERA (Korišćenje konstruktora)
        OpenAiRequest request = new OpenAiRequest(MODEL, messages);

        // 3. Slanje zahteva i prijem odgovora (kao i pre)
        OpenAiResponse response = webClient.post()
                .uri(COMPLETIONS_URI)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAiResponse.class)
                .block();

        return response != null ? response.getFirstResponseContent() : "Error: No valid response from OpenAI.";
    }
}
