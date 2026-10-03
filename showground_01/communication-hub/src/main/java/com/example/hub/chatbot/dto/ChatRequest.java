// ChatRequest.java
package com.example.hub.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank
    private String message;
    @NotBlank
    private String mode = "native"; // native | ai
    @NotBlank
    private String sessionId = "default";
}