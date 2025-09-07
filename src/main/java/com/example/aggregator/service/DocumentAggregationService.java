package com.example.aggregator.service;

import com.example.aggregator.model.DocumentHistory;
import com.example.aggregator.model.dtos.ArchiveDto;
import com.example.aggregator.model.dtos.DocumentDto;
import com.example.aggregator.model.dtos.NotificationDto;
import com.example.aggregator.utils.WebClientUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.Duration;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentAggregationService {

  private final @Qualifier("documentClient") WebClient documentClient;
  private final @Qualifier("archiveClient") WebClient archiveClient;
  private final @Qualifier("notificationClient") WebClient notificationClient;

  @CircuitBreaker(name = "aggregationService", fallbackMethod = "fallbackAggregate")
  public Mono<DocumentHistory> getClientHistory(String clientId) {

    // 🔹 Documents en flux
    Flux<DocumentDto> documentsFlux =
        documentClient
            .get()
            .uri("/api/documents?clientId={id}", clientId)
            .retrieve()
            .bodyToFlux(DocumentDto.class)
            .timeout(Duration.ofSeconds(2))
            .onErrorResume(
                ex -> {
                  log.error("Erreur DocumentService: {}", ex.getMessage());
                  return Flux.empty();
                });

    // 🔹 Archives en flux
    Flux<ArchiveDto> archivesFlux =
        archiveClient
            .get()
            .uri("/api/archive?clientId={id}", clientId)
            .retrieve()
            .bodyToFlux(ArchiveDto.class)
            .timeout(Duration.ofSeconds(2))
            .onErrorResume(
                ex -> {
                  log.error("Erreur ArchiveService: {}", ex.getMessage());
                  return Flux.empty();
                });

    // 🔹 Notifications en flux
    Flux<NotificationDto> notificationsFlux =
        notificationClient
            .get()
            .uri("/api/notifications?clientId={id}", clientId)
            .retrieve()
            .bodyToFlux(NotificationDto.class)
            .timeout(Duration.ofSeconds(2))
            .onErrorResume(
                ex -> {
                  log.error("Erreur NotificationService: {}", ex.getMessage());
                  return Flux.empty();
                });

    // 🔹 Agrégation finale → on convertit en List à la toute fin
    return Mono.zip(
            documentsFlux.collectList(),
            archivesFlux.collectList(),
            notificationsFlux.collectList())
        .map(
            tuple ->
                new DocumentHistory(
                    clientId,
                    tuple.getT1(), // documents
                    tuple.getT2(), // archives
                    tuple.getT3() // notifications
                    ));
  }

  @CircuitBreaker(name = "aggregationService", fallbackMethod = "fallbackAggregate")
  public Mono<DocumentHistory> getClientHistory1(String clientId) {

    Flux<DocumentDto> documentsFlux =
        WebClientUtils.sendGetRequest(
            documentClient,
            "/api/documents?clientId={id}",
            clientId,
            DocumentDto.class,
            "DocumentService");
    Flux<ArchiveDto> archivesFlux =
        WebClientUtils.sendGetRequest(
            archiveClient,
            "/api/archive?clientId={id}",
            clientId,
            ArchiveDto.class,
            "ArchiveService");
    Flux<NotificationDto> notificationsFlux =
        WebClientUtils.sendGetRequest(
            notificationClient,
            "/api/notifications?clientId={id}",
            clientId,
            NotificationDto.class,
            "NotificationService");

    return Mono.zip(
            documentsFlux.collectList(),
            archivesFlux.collectList(),
            notificationsFlux.collectList())
        .map(tuple -> new DocumentHistory(clientId, tuple.getT1(), tuple.getT2(), tuple.getT3()));
  }

  private Mono<DocumentHistory> fallbackAggregate(String clientId, Throwable t) {
    log.warn("Fallback activé pour client {}: {}", clientId, t.getMessage());
    return Mono.just(
        new DocumentHistory(
            clientId, Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
  }
}
