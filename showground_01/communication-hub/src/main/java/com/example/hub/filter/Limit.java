package com.example.hub.filter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.hub.limiter.limit_on_ip.LimitOnIp;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

///interceptor
@Component 
public class Limit extends OncePerRequestFilter{

    private final static Logger logger = Logger.getLogger(Limit.class.getName());

    private final LimitOnIp limitOnIp;

    @Autowired 
    public Limit(LimitOnIp limitOnIp) {
        this.limitOnIp=limitOnIp;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                
        //get ip
        String ip = request.getRemoteAddr();

        if(!limitOnIp.exist(ip)){
           limitOnIp.addNew(ip,new AtomicInteger(10));
           filterChain.doFilter(request, response);
           return;
        }
        AtomicInteger remaining = limitOnIp.getCount(ip);
        if(remaining.intValue()<=0){
        logger.info("Rate Limit hit");
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //response.setHeader("Retry-After", Long.toString(rateLimiter.secondsUntilReset()));
        response.getWriter().write(
                "{\"error\":\"Too Many Requests\",\"message\":\"System saturated. Retry later.\"}");
       
            return;
        }else{
            logger.info("Remaining req " + remaining.intValue());
            remaining.getAndDecrement();
            filterChain.doFilter(request, response);
            //return true;
            return;
        }

       // return true;
    }
}