package com.example.aggregator.service;

import com.example.aggregator.model.DocumentHistory;
import com.example.aggregator.model.dtos.ArchiveDto;
import com.example.aggregator.model.dtos.DocumentDto;
import com.example.aggregator.model.dtos.NotificationDto;
import com.example.aggregator.utils.WebClientUtils;
import com.example.common_lib.model.exception.ServiceUnavailableException1;
import com.example.common_lib.model.response.ApiResponse1;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.Duration;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
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

  public Mono<ApiResponse1<DocumentHistory>> getClientHistory2(String clientId) {

    Flux<DocumentDto> documentsFlux =
        WebClientUtils.sendGetRequest(
                documentClient,
                "/api/documents?clientId={id}",
                clientId,
                DocumentDto.class,
                "DocumentService")
            .onErrorResume(
                ex -> {
                  log.error("DocumentService indisponible: {}", ex.getMessage());
                  return Flux.error(
                      new ServiceUnavailableException1(
                          "DocumentService est indisponible", "/api/documents"));
                });

    Flux<ArchiveDto> archivesFlux =
        WebClientUtils.sendGetRequest(
                archiveClient,
                "/api/archive?clientId={id}",
                clientId,
                ArchiveDto.class,
                "ArchiveService")
            .onErrorResume(
                ex -> {
                  log.error("ArchiveService indisponible: {}", ex.getMessage());
                  return Flux.error(
                      new ServiceUnavailableException1(
                          "ArchiveService est indisponible", "/api/archive"));
                });

    Flux<NotificationDto> notificationsFlux =
        WebClientUtils.sendGetRequest(
                notificationClient,
                "/api/notifications?clientId={id}",
                clientId,
                NotificationDto.class,
                "NotificationService")
            .onErrorResume(
                ex -> {
                  log.error("NotificationService indisponible: {}", ex.getMessage());
                  return Flux.error(
                      new ServiceUnavailableException1(
                          "NotificationService est indisponible", "/api/notifications"));
                });

    return Mono.zip(
            documentsFlux.collectList(),
            archivesFlux.collectList(),
            notificationsFlux.collectList())
        .map(
            tuple -> {
              DocumentHistory history =
                  new DocumentHistory(clientId, tuple.getT1(), tuple.getT2(), tuple.getT3());
              return ApiResponse1.success(
                  "Historique récupéré avec succès", history, HttpStatus.OK.value());
            })
        .onErrorResume(
            ServiceUnavailableException1.class,
            ex -> {
              // Cette partie va être interceptée par ton @ControllerAdvice
              // mais au cas où tu veux renvoyer une ApiResponse1 directe :
              log.warn("Erreur agrégation pour client {}: {}", clientId, ex.getMessage());
              return Mono.just(
                  ApiResponse1.error(
                      ex.getMessage(), ex.getCode(), HttpStatus.SERVICE_UNAVAILABLE.value()));
            })
        .onErrorResume(
            ex -> {
              // pour toute autre erreur générique
              log.error("Erreur inattendue pour client {}: {}", clientId, ex.getMessage());
              return Mono.just(
                  ApiResponse1.error(
                      "Erreur interne lors de l’agrégation",
                      "AGGREGATION_ERROR",
                      HttpStatus.INTERNAL_SERVER_ERROR.value()));
            });
  }
}
