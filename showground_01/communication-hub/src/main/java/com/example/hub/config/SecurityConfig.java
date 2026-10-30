package com.example.hub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.hub.filter.Limit;

@Configuration 
@EnableWebSecurity 
public class SecurityConfig {
    
    @Autowired 
    private  Limit limit;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeRequests(
            auth->auth.requestMatchers("/api/**").permitAll()
        );
        http.addFilterBefore(limit,UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
     
}
