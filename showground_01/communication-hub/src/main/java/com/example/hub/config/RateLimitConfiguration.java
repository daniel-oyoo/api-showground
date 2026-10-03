package com.example.hub.config;

import com.example.hub.limiter.ProcessingTier;
import com.example.hub.limiter.TieredRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

@Configuration
public class RateLimitConfiguration {

    @Bean
    public TieredRateLimiter tieredRateLimiter(
            @Value("${app.rate-limit.instant-capacity:100}") int instantCapacity,
            @Value("${app.rate-limit.async-capacity:200}") int asyncCapacity,
            @Value("${app.rate-limit.manual-capacity:300}") int manualCapacity,
            @Value("${app.rate-limit.window-seconds:60}") long windowSeconds) {
        if (windowSeconds <= 0) {
            throw new IllegalArgumentException("Rate limit window must be positive");
        }
        ProcessingTier manual = new ProcessingTier("Manual", manualCapacity, List.of());
        ProcessingTier async = new ProcessingTier("Async", asyncCapacity, List.of(manual));
        ProcessingTier instant = new ProcessingTier("Instant", instantCapacity, List.of(async));
        return new TieredRateLimiter(instant, Duration.ofSeconds(windowSeconds), Clock.systemUTC());
    }
}
