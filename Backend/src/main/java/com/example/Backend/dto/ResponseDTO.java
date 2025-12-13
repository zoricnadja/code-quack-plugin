package com.example.backend.dto;


public class ResponseDTO { //TODO rename to something better
    private String response;

    public String getResponse() {
        return response;
    }

    public ResponseDTO(String response) {
        this.response = response;
    }
}