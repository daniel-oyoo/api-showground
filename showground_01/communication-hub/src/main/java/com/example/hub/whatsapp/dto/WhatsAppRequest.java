// WhatsAppRequest.java
package com.example.hub.whatsapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WhatsAppRequest {
    @NotBlank
    private String to;
    @NotBlank
    private String message;
}