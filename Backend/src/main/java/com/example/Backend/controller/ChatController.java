package com.example.backend.controller;

import com.example.backend.dto.CreateMessageDTO;
import com.example.backend.dto.MessageResponseDTO;
import com.example.backend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(value ="/messages", produces = "application/json")
    public ResponseEntity<MessageResponseDTO> startSession(@RequestBody CreateMessageDTO questionDTO) {
        String response = chatService.askQuestion(questionDTO);
        return ResponseEntity.ok(new MessageResponseDTO(response));
    }


}
