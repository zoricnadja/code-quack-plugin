package com.example.backend.model;

import java.util.List;

public class OpenAiRequest {
    private String model;
    private List<Message> messages;

    public OpenAiRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    public OpenAiRequest() {}

    public String getModel() {
        return model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public static class Message {
        private String role; // "system", "user", ili "assistant"
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public Message() {}

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
