package com.example.aggregator.web;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockAuthentication;

import com.example.aggregator.model.DocumentHistory;
import com.example.aggregator.model.dtos.ArchiveDto;
import com.example.aggregator.model.dtos.DocumentDto;
import com.example.aggregator.model.dtos.NotificationDto;
import com.example.aggregator.security.SecurityConfig;
import com.example.aggregator.service.DocumentAggregationService;
import com.example.common_lib.model.response.ApiResponse1;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = DocumentAggregationController.class)
@Import({DocumentAggregationService.class, SecurityConfig.class})
class DocumentControllerIntegrationTest {

  @Autowired private WebTestClient webTestClient;

  @MockBean private DocumentAggregationService service;

  @Test
  void testAccessWithAgentAuthority() {
    DocumentHistory history =
        new DocumentHistory(
            "client1",
            List.of(new DocumentDto("doc1", "", "")),
            List.of(new ArchiveDto("archive1", "", "")),
            List.of(new NotificationDto("notif1", "", "", "")));

    Mockito.when(service.getClientHistory("client1"))
        .thenReturn(Mono.just(ApiResponse1.success("OK", history, 200)));

    webTestClient
        .mutateWith(
            mockAuthentication(
                new TestingAuthenticationToken("fabrice", "1234", "agent") // rôle "agent"
                ))
        .get()
        .uri("/api/clients/client1/document-history")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("OK")
        .jsonPath("$.data.clientId")
        .isEqualTo("client1");
  }

  @Test
  void testAccessWithoutAgentAuthority() {
    webTestClient
        .mutateWith(
            mockAuthentication(
                new TestingAuthenticationToken("fabrice", "1234", "user") // rôle "user"
                ))
        .get()
        .uri("/api/clients/client1/document-history")
        .exchange()
        .expectStatus()
        .isForbidden(); // 403 FORBIDDEN
  }

  @Test
  void testServiceUnavailable() {
    Mockito.when(service.getClientHistory("client1"))
        .thenReturn(
            Mono.just(ApiResponse1.error("DocumentService est indisponible", "DOC_ERR", 503)));

    webTestClient
        .mutateWith(mockAuthentication(new TestingAuthenticationToken("fabrice", "1234", "agent")))
        .get()
        .uri("/api/clients/client1/document-history")
        .exchange()
        .expectStatus()
        .isEqualTo(503)
        .expectBody()
        .jsonPath("$.message")
        .isEqualTo("DocumentService est indisponible")
        .jsonPath("$.statusCode")
        .isEqualTo(503);
  }

  private String generateJwtWithRole(String role) {
    // ✅ Ici tu peux générer un JWT mock pour tests unitaires
    // Exemple simplifié avec lib comme 'nimbus-jose-jwt' ou Spring Security test
    return "mock-jwt-with-" + role;
  }
}
