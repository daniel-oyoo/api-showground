// WhatsAppController.java
package com.example.hub.whatsapp.controller;



import com.example.hub.whatsapp.dto.WhatsAppRequest;
import com.example.hub.whatsapp.service.WhatsAppService;

import com.example.hub.api.ApiResponse;
import com.example.hub.limiter.RateLimitInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/whatsapp", "/api/v1/whatsapp"})
@RequiredArgsConstructor
@CrossOrigin
public class WhatsAppController {
    private final WhatsAppService service;

    @PostMapping("/send")
    public ResponseEntity<?> send(@Valid @RequestBody WhatsAppRequest req, HttpServletRequest request) {
        String result = service.send(req);
        if (request.getRequestURI().contains("/api/v1/")) {
            String tier = (String) request.getAttribute(RateLimitInterceptor.RATE_LIMIT_TIER);
            return ResponseEntity.ok(new ApiResponse<>(result, tier));
        }
        return ResponseEntity.ok(result);
    }
}