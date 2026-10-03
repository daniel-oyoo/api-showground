package com.example.hub.limiter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitInterceptorTest {

    @Test
    void attachesTierForAcceptedRequestsAndReturnsStructured429WhenFull() throws Exception {
        ProcessingTier root = new ProcessingTier("Instant", 1, List.of());
        TieredRateLimiter limiter = new TieredRateLimiter(root, Duration.ofMinutes(1), Clock.systemUTC());
        RateLimitInterceptor interceptor = new RateLimitInterceptor(limiter);

        MockHttpServletRequest acceptedRequest = new MockHttpServletRequest("POST", "/api/v1/chat");
        MockHttpServletResponse acceptedResponse = new MockHttpServletResponse();
        assertThat(interceptor.preHandle(acceptedRequest, acceptedResponse, new Object())).isTrue();
        assertThat(acceptedRequest.getAttribute(RateLimitInterceptor.RATE_LIMIT_TIER)).isEqualTo("Instant");

        MockHttpServletRequest rejectedRequest = new MockHttpServletRequest("POST", "/api/v1/chat");
        MockHttpServletResponse rejectedResponse = new MockHttpServletResponse();
        assertThat(interceptor.preHandle(rejectedRequest, rejectedResponse, new Object())).isFalse();
        assertThat(rejectedResponse.getStatus()).isEqualTo(429);
        assertThat(rejectedResponse.getContentType()).isEqualTo("application/json");
        assertThat(rejectedResponse.getHeader("Retry-After")).isNotNull();
        assertThat(rejectedResponse.getContentAsString()).isEqualTo(
                "{\"error\":\"Too Many Requests\",\"message\":\"System saturated. Retry later.\"}");
    }
}
