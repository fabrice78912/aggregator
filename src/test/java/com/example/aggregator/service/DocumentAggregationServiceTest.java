package com.example.aggregator.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import com.example.aggregator.model.DocumentHistory;
import com.example.aggregator.model.dtos.ArchiveDto;
import com.example.aggregator.model.dtos.DocumentDto;
import com.example.aggregator.model.dtos.NotificationDto;
import com.example.aggregator.utils.WebClientUtils;
import com.example.common_lib.model.response.ApiResponse1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class DocumentAggregationServiceTest {

  @Mock private WebClient documentClient;
  @Mock private WebClient archiveClient;
  @Mock private WebClient notificationClient;

  @InjectMocks private DocumentAggregationService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetClientHistory_Success() {
    // Initialise le service avec les clients mockés
    service = new DocumentAggregationService(documentClient, archiveClient, notificationClient);

    // Mock statique pour WebClientUtils
    try (MockedStatic<WebClientUtils> utilities = mockStatic(WebClientUtils.class)) {

      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      documentClient,
                      "/api/documents?clientId={id}",
                      "client1",
                      DocumentDto.class,
                      "DocumentService"))
          .thenReturn(Flux.just(new DocumentDto("doc1", "", "")));

      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      archiveClient,
                      "/api/archive?clientId={id}",
                      "client1",
                      ArchiveDto.class,
                      "ArchiveService"))
          .thenReturn(Flux.just(new ArchiveDto("archive1", "", "")));

      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      notificationClient,
                      "/api/notifications?clientId={id}",
                      "client1",
                      NotificationDto.class,
                      "NotificationService"))
          .thenReturn(Flux.just(new NotificationDto("notif1", "", "", "")));

      Mono<ApiResponse1<DocumentHistory>> result = service.getClientHistory("client1");

      StepVerifier.create(result)
          .expectNextMatches(
              resp ->
                  resp.isSuccess()
                      && "Historique récupéré avec succès".equals(resp.getMessage())
                      && "client1".equals(resp.getData().getClientId())
                      && resp.getStatusCode() == 200)
          .verifyComplete();
    }
  }

  @Test
  void testGetClientHistory_DocumentServiceUnavailable() {
    service = new DocumentAggregationService(documentClient, archiveClient, notificationClient);

    try (MockedStatic<WebClientUtils> utilities = mockStatic(WebClientUtils.class)) {

      // DocumentService renvoie une erreur
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      documentClient,
                      "/api/documents?clientId={id}",
                      "client1",
                      DocumentDto.class,
                      "DocumentService"))
          .thenReturn(Flux.error(new RuntimeException("Service down")));

      // Les autres services renvoient vide
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      archiveClient,
                      "/api/archive?clientId={id}",
                      "client1",
                      ArchiveDto.class,
                      "ArchiveService"))
          .thenReturn(Flux.empty());

      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      notificationClient,
                      "/api/notifications?clientId={id}",
                      "client1",
                      NotificationDto.class,
                      "NotificationService"))
          .thenReturn(Flux.empty());

      Mono<ApiResponse1<DocumentHistory>> result = service.getClientHistory("client1");

      StepVerifier.create(result)
          .expectNextMatches(
              resp ->
                  !resp.isSuccess()
                      && resp.getMessage().equals("DocumentService est indisponible")
                      && resp.getStatusCode() == 503)
          .verifyComplete();
    }
  }

  @Test
  void testGetClientHistory_ArchiveServiceUnavailable() {
    service = new DocumentAggregationService(documentClient, archiveClient, notificationClient);

    try (MockedStatic<WebClientUtils> utilities = mockStatic(WebClientUtils.class)) {

      // DocumentService renvoie vide
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      documentClient,
                      "/api/documents?clientId={id}",
                      "client1",
                      DocumentDto.class,
                      "DocumentService"))
          .thenReturn(Flux.empty());

      // ArchiveService renvoie une erreur
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      archiveClient,
                      "/api/archive?clientId={id}",
                      "client1",
                      ArchiveDto.class,
                      "ArchiveService"))
          .thenReturn(Flux.error(new RuntimeException("Service down")));

      // NotificationService renvoie vide
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      notificationClient,
                      "/api/notifications?clientId={id}",
                      "client1",
                      NotificationDto.class,
                      "NotificationService"))
          .thenReturn(Flux.empty());

      Mono<ApiResponse1<DocumentHistory>> result = service.getClientHistory("client1");

      StepVerifier.create(result)
          .expectNextMatches(
              resp ->
                  !resp.isSuccess()
                      && resp.getMessage().equals("ArchiveService est indisponible")
                      && resp.getStatusCode() == 503)
          .verifyComplete();
    }
  }

  @Test
  void testGetClientHistory_NotificationServiceUnavailable() {
    service = new DocumentAggregationService(documentClient, archiveClient, notificationClient);

    try (MockedStatic<WebClientUtils> utilities = mockStatic(WebClientUtils.class)) {

      // DocumentService renvoie vide
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      documentClient,
                      "/api/documents?clientId={id}",
                      "client1",
                      DocumentDto.class,
                      "DocumentService"))
          .thenReturn(Flux.empty());

      // ArchiveService renvoie vide
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      archiveClient,
                      "/api/archive?clientId={id}",
                      "client1",
                      ArchiveDto.class,
                      "ArchiveService"))
          .thenReturn(Flux.empty());

      // NotificationService renvoie une erreur
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      notificationClient,
                      "/api/notifications?clientId={id}",
                      "client1",
                      NotificationDto.class,
                      "NotificationService"))
          .thenReturn(Flux.error(new RuntimeException("Service down")));

      Mono<ApiResponse1<DocumentHistory>> result = service.getClientHistory("client1");

      StepVerifier.create(result)
          .expectNextMatches(
              resp ->
                  !resp.isSuccess()
                      && resp.getMessage().equals("NotificationService est indisponible")
                      && resp.getStatusCode() == 503)
          .verifyComplete();
    }
  }

  /* @Test
      void testGetClientHistory_UnexpectedError() {
          service = new DocumentAggregationService(documentClient, archiveClient, notificationClient);

          try (MockedStatic<WebClientUtils> utilities = mockStatic(WebClientUtils.class)) {
              utilities.when(() ->
                      WebClientUtils.sendGetRequest(
                              ArgumentMatchers.<WebClient>any(),
                              ArgumentMatchers.<String>any(),
                              ArgumentMatchers.<String>any(),
                              ArgumentMatchers.<Class<DocumentDto>>any(),
                              ArgumentMatchers.<String>any())
              ).thenAnswer(invocation -> Flux.error(new RuntimeException("Unexpected")));

              utilities.when(() ->
                      WebClientUtils.sendGetRequest(
                              ArgumentMatchers.<WebClient>any(),
                              ArgumentMatchers.<String>any(),
                              ArgumentMatchers.<String>any(),
                              ArgumentMatchers.<Class<ArchiveDto>>any(),
                              ArgumentMatchers.<String>any())
              ).thenAnswer(invocation -> Flux.error(new RuntimeException("Unexpected")));

              utilities.when(() ->
                      WebClientUtils.sendGetRequest(
                              ArgumentMatchers.<WebClient>any(),
                              ArgumentMatchers.<String>any(),
                              ArgumentMatchers.<String>any(),
                              ArgumentMatchers.<Class<NotificationDto>>any(),
                              ArgumentMatchers.<String>any())
              ).thenAnswer(invocation -> Flux.error(new RuntimeException("Unexpected")));

              Mono<ApiResponse1<DocumentHistory>> result = service.getClientHistory("client1");

              StepVerifier.create(result)
                      .expectNextMatches(resp ->
                              !resp.isSuccess() &&
                                      resp.getMessage().equals("Erreur interne lors de l’agrégation") &&
                                      resp.getErrorCode().equals("AGGREGATION_ERROR") &&
                                      resp.getStatusCode() == 500
                      )
                      .verifyComplete();
          }
      }
  */
  @Test
  void testFallbackAggregate() {
    service = new DocumentAggregationService(documentClient, archiveClient, notificationClient);

    try (MockedStatic<WebClientUtils> utilities = mockStatic(WebClientUtils.class)) {
      // Mock statique pour DocumentDto
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      any(WebClient.class),
                      any(String.class),
                      any(String.class),
                      eq(DocumentDto.class),
                      any(String.class)))
          .thenReturn(Flux.error(new RuntimeException("DocumentService down")));

      // Mock statique pour ArchiveDto
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      any(WebClient.class),
                      any(String.class),
                      any(String.class),
                      eq(ArchiveDto.class),
                      any(String.class)))
          .thenReturn(Flux.error(new RuntimeException("ArchiveService down")));

      // Mock statique pour NotificationDto
      utilities
          .when(
              () ->
                  WebClientUtils.sendGetRequest(
                      any(WebClient.class),
                      any(String.class),
                      any(String.class),
                      eq(NotificationDto.class),
                      any(String.class)))
          .thenReturn(Flux.error(new RuntimeException("NotificationService down")));

      Mono<ApiResponse1<DocumentHistory>> result = service.getClientHistory("client1");

      StepVerifier.create(result)
          .expectNextMatches(
              resp ->
                  !resp.isSuccess()
                      && resp.getStatusCode() == 503
                      && (resp.getMessage().contains("DocumentService est indisponible")
                          || resp.getMessage().contains("ArchiveService est indisponible")
                          || resp.getMessage().contains("NotificationService est indisponible")))
          .verifyComplete();
    }
  }
}
