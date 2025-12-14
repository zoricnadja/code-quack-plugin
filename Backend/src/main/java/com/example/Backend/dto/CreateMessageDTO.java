package com.example.backend.dto;

import com.example.backend.model.OpenAiRequest;

import java.util.List;

public class CreateMessageDTO {
    private List<OpenAiRequest.Message> history;
    private String codeContext;
    private String userProblem;

    public String getCodeContext() {
        return codeContext;
    }

    public String getUserProblem() {
        return userProblem;
    }

    public List<OpenAiRequest.Message> getHistory() {
        return history;
    }
}