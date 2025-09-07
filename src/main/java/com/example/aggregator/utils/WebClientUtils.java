package com.example.aggregator.utils;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
public class WebClientUtils {

  /**
   * Méthode générique pour appeler un microservice avec WebClient, gérer les erreurs et les 503.
   */
  public static <T> Flux<T> callService(
      WebClient client, String uri, String clientId, Class<T> clazz, String serviceName) {
    return client
        .get()
        .uri(uri, clientId)
        .exchangeToFlux(
            response -> {
              if (response.statusCode().is2xxSuccessful()) {
                return response.bodyToFlux(clazz);
              } else if (response.statusCode().value() == 503) {
                log.warn("{} indisponible (503) pour client {}", serviceName, clientId);
                return Flux.error(new RuntimeException(serviceName + " indisponible"));
              } else {
                return response
                    .bodyToMono(String.class)
                    .flatMapMany(
                        body -> {
                          log.error("{} HTTP {} - {}", serviceName, response.statusCode(), body);
                          return Flux.error(new RuntimeException(serviceName + " error: " + body));
                        });
              }
            })
        .timeout(Duration.ofSeconds(2))
        .onErrorResume(
            ex -> {
              log.error("Erreur lors de l'appel {}: {}", serviceName, ex.getMessage());
              return Flux.empty();
            });
  }
}
