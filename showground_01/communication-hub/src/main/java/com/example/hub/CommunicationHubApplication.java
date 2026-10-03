package com.example.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CommunicationHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunicationHubApplication.class, args);
    }
}