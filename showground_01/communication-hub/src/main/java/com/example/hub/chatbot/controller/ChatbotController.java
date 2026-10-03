// ChatbotController.java
package com.example.hub.chatbot.controller;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hub.api.ApiResponse;
import com.example.hub.chatbot.dto.ChatRequest;
import com.example.hub.limiter.RateLimitInterceptor;
import com.example.hub.chatbot.service.AiChatbotService;
import com.example.hub.chatbot.service.NativeChatbotService;

import java.util.Map;

@RestController
@RequestMapping({"/api/chat", "/api/v1/chat"})
@RequiredArgsConstructor
@CrossOrigin
public class ChatbotController {

    private final NativeChatbotService nativeBot;
    private final AiChatbotService aiBot;

    @PostMapping
    public ResponseEntity<?> chat(@Valid @RequestBody ChatRequest req, HttpServletRequest request) {
        String reply = "ai".equalsIgnoreCase(req.getMode())
                ? aiBot.reply(req.getMessage())
                : nativeBot.reply(req.getSessionId(), req.getMessage());
        Map<String, String> body = Map.of("reply", reply, "mode", req.getMode());
        if (request.getRequestURI().contains("/api/v1/")) {
            String tier = (String) request.getAttribute(RateLimitInterceptor.RATE_LIMIT_TIER);
            return ResponseEntity.ok(new ApiResponse<>(body, tier));
        }
        return ResponseEntity.ok(body);
    }
}