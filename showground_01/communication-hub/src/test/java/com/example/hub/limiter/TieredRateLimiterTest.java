package com.example.hub.limiter;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class TieredRateLimiterTest {

    @Test
    void allocatesGreedilyAcrossConfiguredTiers() {
        TieredRateLimiter limiter = limiter(1, 2, 1, new MutableClock());

        assertThat(limiter.tryAcquire()).contains("Instant");
        assertThat(limiter.tryAcquire()).contains("Async");
        assertThat(limiter.tryAcquire()).contains("Async");
        assertThat(limiter.tryAcquire()).contains("Manual");
        assertThat(limiter.tryAcquire()).isEmpty();
    }

    @Test
    void resetsAllTierBudgetsAtTheEndOfTheWindow() {
        MutableClock clock = new MutableClock();
        TieredRateLimiter limiter = limiter(1, 0, 0, clock);

        assertThat(limiter.tryAcquire()).contains("Instant");
        assertThat(limiter.tryAcquire()).isEmpty();
        clock.advance(Duration.ofMinutes(1));
        assertThat(limiter.tryAcquire()).contains("Instant");
    }

    private TieredRateLimiter limiter(int instant, int async, int manual, Clock clock) {
        ProcessingTier manualTier = new ProcessingTier("Manual", manual, List.of());
        ProcessingTier asyncTier = new ProcessingTier("Async", async, List.of(manualTier));
        ProcessingTier instantTier = new ProcessingTier("Instant", instant, List.of(asyncTier));
        return new TieredRateLimiter(instantTier, Duration.ofMinutes(1), clock);
    }

    private static final class MutableClock extends Clock {
        private final AtomicReference<Instant> instant =
                new AtomicReference<>(Instant.parse("2026-01-01T00:00:00Z"));

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant.get();
        }

        void advance(Duration duration) {
            instant.updateAndGet(current -> current.plus(duration));
        }
    }
}
