package com.example.datnbe.base.controller;

import com.example.datnbe.base.entity.ChatRequest;
import com.example.datnbe.base.service.ChatBotService;
import com.example.datnbe.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController

public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping("/chatbot")
    public ApiResponse<String> chat(@RequestBody ChatRequest request) {
        return chatBotService.getResponseFromGemini(request.getHistory());
    }
}
