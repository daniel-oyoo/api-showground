package com.example.hub.limiter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    public static final String RATE_LIMIT_TIER = "RATE_LIMIT_TIER";

    private final TieredRateLimiter rateLimiter;

    public RateLimitInterceptor(TieredRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        Optional<String> tier = rateLimiter.tryAcquire();
        if (tier.isPresent()) {
            request.setAttribute(RATE_LIMIT_TIER, tier.get());
            return true;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", Long.toString(rateLimiter.secondsUntilReset()));
        response.getWriter().write(
                "{\"error\":\"Too Many Requests\",\"message\":\"System saturated. Retry later.\"}");
        return false;
    }
}
