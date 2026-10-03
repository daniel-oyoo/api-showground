// SmsController.java
package com.example.hub.sms.controller;

import com.example.hub.sms.dto.SmsRequest;
import com.example.hub.sms.service.SmsService;

import com.example.hub.api.ApiResponse;
import com.example.hub.limiter.RateLimitInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/sms", "/api/v1/sms"})
@RequiredArgsConstructor
@CrossOrigin
public class SmsController {
    private final SmsService service;

    @PostMapping("/send")
    public ResponseEntity<?> send(@Valid @RequestBody SmsRequest req, HttpServletRequest request) {
        String result = service.send(req);
        if (request.getRequestURI().contains("/api/v1/")) {
            String tier = (String) request.getAttribute(RateLimitInterceptor.RATE_LIMIT_TIER);
            return ResponseEntity.ok(new ApiResponse<>(result, tier));
        }
        return ResponseEntity.ok(result);
    }
}