package com.example.aggregator.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @Qualifier("documentClient")
    public WebClient documentClient(@Value("${config.document-service}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    @Qualifier("archiveClient")
    public WebClient archiveClient(@Value("${config.archiving-service}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    @Qualifier("notificationClient")
    public WebClient notificationClient(@Value("${config.notification-service}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

}

