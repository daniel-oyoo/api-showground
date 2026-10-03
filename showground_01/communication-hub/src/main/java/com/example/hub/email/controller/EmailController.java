// EmailController.java
 
package com.example.hub.email.controller;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hub.api.ApiResponse;
import com.example.hub.email.dto.EmailRequest;
import com.example.hub.email.service.EmailService;
import com.example.hub.limiter.RateLimitInterceptor;

@RestController
@RequestMapping({"/api/email", "/api/v1/email"})
@RequiredArgsConstructor
@CrossOrigin
public class EmailController {
    private final EmailService service;

    @PostMapping("/send")
    public ResponseEntity<?> send(@Valid @RequestBody EmailRequest req, HttpServletRequest request) {
        String result = service.send(req);
        if (request.getRequestURI().contains("/api/v1/")) {
            String tier = (String) request.getAttribute(RateLimitInterceptor.RATE_LIMIT_TIER);
            return ResponseEntity.ok(new ApiResponse<>(result, tier));
        }
        return ResponseEntity.ok(result);
    }
}
