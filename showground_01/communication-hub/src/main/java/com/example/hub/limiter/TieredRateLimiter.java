package com.example.hub.limiter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TieredRateLimiter {

    private final ProcessingTier root;
    private final Duration window;
    private final Clock clock;
    private final Map<String, Integer> usage = new LinkedHashMap<>();
    private Instant windowEndsAt;

    public TieredRateLimiter(ProcessingTier root, Duration window, Clock clock) {
        if (root == null || window == null || clock == null) {
            throw new IllegalArgumentException("Tier, window, and clock are required");
        }
        if (window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("Rate limit window must be positive");
        }
        this.root = root;
        this.window = window;
        this.clock = clock;
        initializeUsage(root);
    }

    public synchronized Optional<String> tryAcquire() {
        Instant now = clock.instant();
        if (windowEndsAt == null || !now.isBefore(windowEndsAt)) {
            usage.replaceAll((tier, count) -> 0);
            windowEndsAt = now.plus(window);
        }
        return acquireFrom(root);
    }

    public synchronized long secondsUntilReset() {
        if (windowEndsAt == null) {
            return 0;
        }
        long remainingMillis = Duration.between(clock.instant(), windowEndsAt).toMillis();
        return Math.max(1, (remainingMillis + 999) / 1000);
    }

    private Optional<String> acquireFrom(ProcessingTier tier) {
        int currentUsage = usage.get(tier.name());
        if (currentUsage < tier.capacity()) {
            usage.put(tier.name(), currentUsage + 1);
            return Optional.of(tier.name());
        }
        for (ProcessingTier next : tier.nextOptions()) {
            Optional<String> allocation = acquireFrom(next);
            if (allocation.isPresent()) {
                return allocation;
            }
        }
        return Optional.empty();
    }

    private void initializeUsage(ProcessingTier tier) {
        if (usage.putIfAbsent(tier.name(), 0) != null) {
            throw new IllegalArgumentException("Tier names must be unique: " + tier.name());
        }
        List<ProcessingTier> nextOptions = tier.nextOptions();
        for (ProcessingTier next : nextOptions) {
            initializeUsage(next);
        }
    }
}
