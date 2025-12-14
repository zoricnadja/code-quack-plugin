package org.example.model;

import java.util.Collections;
import java.util.List;

public class ChatRequest {
    private List<Message> history;
    private String codeContext;
    private String userProblem;

    public ChatRequest(List<Message> history ,String codeContext, String userProblem) {
        this.history = history;
        this.codeContext = codeContext;
        this.userProblem = userProblem;
    }
}
