package com.example.hub.api;

public record ApiResponse<T>(T data, String rateLimitTier) {
}
