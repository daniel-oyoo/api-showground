package com.example.hub.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class AppConfig {

    /**
     * Shared RestClient with sensible timeouts.
     * Inject as: private final RestClient restClient;
     */
    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(30));

        return builder
                .requestFactory(factory)
                .build();
    }

    /**
     * Optional: apply the same timeouts to any RestClient.Builder
     * auto-injected elsewhere.
     */
    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return builder -> builder.requestFactory(
                new JdkClientHttpRequestFactory(
                        HttpClient.newBuilder()
                                .connectTimeout(Duration.ofSeconds(10))
                                .build()
                )
        );
    }
}