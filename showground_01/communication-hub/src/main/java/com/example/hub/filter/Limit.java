package com.example.hub.filter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.hub.limiter.limit_on_ip.LimitOnIp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Enterprise-grade Rate Limiting Filter for IP-based throttling.
 * Intercepts incoming HTTP requests to protect downstream resources from saturation.
 */
@Component 
@Order(10)
public class Limit extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(Limit.class.getName());
    private static final int DEFAULT_REQUEST_LIMIT = 10;

    private final LimitOnIp limitOnIp;

    @Autowired 
    public Limit(LimitOnIp limitOnIp) {
        this.limitOnIp = limitOnIp;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Extract client IP, respecting standard proxy headers if deployed behind an ALB/Nginx
        String ip = resolveClientIp(request);

        // Ensure the IP exists in tracking; if not, initialize with quota
        if (!limitOnIp.exist(ip)) {
            limitOnIp.addNew(ip, new AtomicInteger(DEFAULT_REQUEST_LIMIT));
        }

        AtomicInteger remaining = limitOnIp.getCount(ip);

        /**
         * ATOMIC DECREMENT & CHECK:
         * Using updateAndGet ensures thread-safe atomic decrement to prevent 
         * race conditions under concurrent load testing.
         */
        int currentRemaining = remaining.updateAndGet(val -> val > 0 ? val - 1 : 0);

        if (currentRemaining == 0 && remaining.get() <= 0) {
            // Rate limit exceeded: Block request and return 429 Too Many Requests
            logger.warning("Rate limit hit for IP: " + ip);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                "{\"error\":\"Too Many Requests\",\"message\":\"System saturated. Retry later.\"}"
            );
            return;
        }

        // Request allowed: Proceed down the filter chain
        logger.info("IP: " + ip + " | Remaining quota: " + currentRemaining);
        filterChain.doFilter(request, response);
    }

    /**
     * Resolves the real client IP, falling back to remote address if proxy headers are missing.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}