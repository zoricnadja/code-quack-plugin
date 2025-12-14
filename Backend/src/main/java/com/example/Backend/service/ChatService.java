package com.example.backend.service;

import com.example.backend.dto.CreateMessageDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.backend.model.OpenAiRequest;
import com.example.backend.model.OpenAiRequest.Message;
import com.example.backend.model.OpenAiResponse;


import java.util.*;

import java.util.List;

@Service
public class ChatService {
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";
    private static final String COMPLETIONS_URI = "/chat/completions";

    private final WebClient webClient;

    private static final String SYSTEM_PROMPT = """
        You are an expert software engineer acting as a 'Rubber Duck'.
        Your Goal is to help the user debug their code by guiding them to the solution themselves.
        DO NOT provide the solution or fixed code. Only ask one or two probing questions per response that will lead the user to find the solution on their own.
        """;

    public ChatService(WebClient openAiWebClient) {
        this.webClient = openAiWebClient;
    }

    public String askQuestion(CreateMessageDTO messageDTO) {

        List<Message> allMessages = new ArrayList<>();


        allMessages.add(new Message("system", SYSTEM_PROMPT));

        if (messageDTO.getHistory() != null && !messageDTO.getHistory().isEmpty()) {
            allMessages.addAll(messageDTO.getHistory());
        }

        String userContent = String.format("Code Snippet:\n```java\n%s\n```\n\nMy Problem: %s",
                messageDTO.getCodeContext(), messageDTO.getUserProblem());

        Message userMessage = new Message("user", userContent);

        allMessages.add(userMessage);

        OpenAiRequest request = new OpenAiRequest(MODEL, allMessages);

        OpenAiResponse response = webClient.post()
                .uri(COMPLETIONS_URI)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OpenAiResponse.class)
                .block();

        return response != null ? response.getFirstResponseContent() : "Error: No valid response from OpenAI.";
    }
}
