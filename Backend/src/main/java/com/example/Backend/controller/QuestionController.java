package com.example.backend.controller;

import com.example.backend.dto.CreateQuestionDTO;
import com.example.backend.dto.ResponseDTO;
import com.example.backend.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping(value ="/debug", produces = "application/json")
    public ResponseEntity<ResponseDTO> startSession(@RequestBody CreateQuestionDTO questionDTO) {
        String response = questionService.askQuestion(questionDTO.getCodeContext(), questionDTO.getUserProblem());
        return ResponseEntity.ok(new ResponseDTO(response));
    }


}
