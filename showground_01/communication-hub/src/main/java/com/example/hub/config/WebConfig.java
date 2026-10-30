package com.example.hub.config;

import com.example.hub.limiter.RateLimitInterceptor;
import com.example.hub.limiter.limit_on_ip.LimitOnIp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;
    private final com.example.hub.limiter.limit_on_ip.LimitOnIpInterceptor limitOnIpInterceptor;

    @Autowired 
    public WebConfig(RateLimitInterceptor rateLimitInterceptor,
        com.example.hub.limiter.limit_on_ip.LimitOnIpInterceptor limitOnIpInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
        this.limitOnIpInterceptor = limitOnIpInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/api/**");
        //registry.addInterceptor(limitOnIpInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}