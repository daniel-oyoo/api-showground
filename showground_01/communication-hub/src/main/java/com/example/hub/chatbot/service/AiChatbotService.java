package com.example.hub.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AiChatbotService {

    @Value("${app.gemini.api-key}") private String apiKey;
    @Value("${app.gemini.url}") private String url;
    @Value("${app.gemini.model}") private String model;

    private final RestClient client = RestClient.create();

    public String reply(String message) {
        if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("your-")) {
            return "[AI mode disabled — set GEMINI_API_KEY] Echo: " + message;
        }

        Map<String, Object> body = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(
                                Map.of("text", "You are a helpful assistant.")
                        )
                ),
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(Map.of("text", message))
                        )
                )
        );

        try {
            Map response = client.post()
                    .uri(url + "/v1beta/models/" + model + ":generateContent")
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            var candidates = (List<Map<String, Object>>) response.get("candidates");
            var content = (Map<String, Object>) candidates.get(0).get("content");
            var parts = (List<Map<String, Object>>) content.get("parts");
            return parts.get(0).get("text").toString();
        } catch (Exception e) {
            return "AI error: " + e.getMessage();
        }
    }
}