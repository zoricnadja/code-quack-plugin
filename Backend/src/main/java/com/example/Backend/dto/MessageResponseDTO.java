package com.example.backend.dto;


public class MessageResponseDTO { //TODO rename to something better
    private String response;

    public String getResponse() {
        return response;
    }

    public MessageResponseDTO(String response) {
        this.response = response;
    }
}