package com.example.aggregator.service;

import com.example.aggregator.model.DocumentHistory;
import com.example.aggregator.model.dtos.ArchiveDto;
import com.example.aggregator.model.dtos.DocumentDto;
import com.example.aggregator.model.dtos.NotificationDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentAggregationService {

    private final @Qualifier("documentClient") WebClient documentClient;
    private final @Qualifier("archiveClient") WebClient archiveClient;
    private final @Qualifier("notificationClient") WebClient notificationClient;


    @CircuitBreaker(name = "aggregationService", fallbackMethod = "fallbackAggregate")
    public Mono<DocumentHistory> getClientHistory(String clientId) {
        Mono<List<DocumentDto>> documents = documentClient.get()
                .uri("/api/documents?clientId={id}", clientId)
                .retrieve()
                .bodyToFlux(DocumentDto.class)
                .collectList()
                .timeout(Duration.ofSeconds(2))
                .onErrorReturn(Collections.emptyList());

        Mono<List<ArchiveDto>> archives = archiveClient.get()
                .uri("/api/archive?clientId={id}", clientId)
                .retrieve()
                .bodyToFlux(ArchiveDto.class)
                .collectList()
                .timeout(Duration.ofSeconds(2))
                .onErrorReturn(Collections.emptyList());

        Mono<List<NotificationDto>> notifications = notificationClient.get()
                .uri("/api/notifications?clientId={id}", clientId)
                .retrieve()
                .bodyToFlux(NotificationDto.class)
                .collectList()
                .timeout(Duration.ofSeconds(2))
                .onErrorReturn(Collections.emptyList());

        return Mono.zip(documents, archives, notifications)
                .map(tuple -> new DocumentHistory(
                        clientId,
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3()
                ));
    }

    private Mono<DocumentHistory> fallbackAggregate(String clientId, Throwable t) {
        log.info("Fallback activé pour client {}: {}", clientId, t.getMessage());
        return Mono.just(new DocumentHistory(clientId, Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
    }
}
