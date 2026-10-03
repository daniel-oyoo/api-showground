package com.example.hub.limiter;

import java.util.List;
import java.util.Objects;

public record ProcessingTier(String name, int capacity, List<ProcessingTier> nextOptions) {
    public ProcessingTier {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tier name must not be blank");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Tier capacity must not be negative");
        }
        nextOptions = List.copyOf(Objects.requireNonNull(nextOptions, "nextOptions"));
    }
}
