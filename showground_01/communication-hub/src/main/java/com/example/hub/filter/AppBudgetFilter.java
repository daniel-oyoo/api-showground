package com.example.hub.filter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
@Order(1)
public class AppBudgetFilter extends OncePerRequestFilter {
    private final AtomicInteger totalCount = new AtomicInteger(0);
    private final int APP_BUDGET = 1000;//total request
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if(totalCount.get()>=APP_BUDGET){
            response.setStatus(503);
            response.getWriter().write("App budget exhausted");
            return;
        }

        totalCount.incrementAndGet();
        filterChain.doFilter(request, response);
    }
    
}
