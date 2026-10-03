// SmsRequest.java
package com.example.hub.sms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SmsRequest {
    @NotBlank
    private String to;
    @NotBlank
    private String message;
}