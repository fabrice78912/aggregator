package com.example.aggregator.web;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class DocumentAggregationControllerIT {

   /* @Container
    static GenericContainer<?> wiremock =
            new GenericContainer<>("wiremock/wiremock:3.9.2")
                    .withExposedPorts(8080);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        String url = "http://" + wiremock.getHost() + ":" + wiremock.getMappedPort(8080);
        registry.add("document.api.url", () -> url);
        registry.add("archive.api.url", () -> url);
        registry.add("notification.api.url", () -> url);
    }

    @Autowired
    private WebTestClient webTestClient;

    private void stubService(String path, String queryParamJson, String responseJson) throws IOException, InterruptedException {
        String stubUrl = "http://" + wiremock.getHost() + ":" + wiremock.getMappedPort(8080) + "/__admin/mappings";
        String body = """
                {
                  "request": {
                    "method": "GET",
                    "urlPath": "%s",
                    "queryParameters": %s
                  },
                  "response": {
                    "status": 200,
                    "headers": { "Content-Type": "application/json" },
                    "body": "%s"
                  }
                }
                """.formatted(path, queryParamJson, responseJson.replace("\"", "\\\""));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(stubUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeEach
    void setupStubs() throws IOException, InterruptedException {
        // Stub DocumentService
        stubService(
                "/api/documents",
                "{ \"clientId\": { \"equalTo\": \"123\" } }",
                "[{\"id\":\"doc1\",\"title\":\"Document 1\",\"status\":\"OK\"}]"
        );

        // Stub ArchiveService
        stubService(
                "/api/archive",
                "{ \"clientId\": { \"equalTo\": \"123\" } }",
                "[{\"id\":\"arch1\",\"documentId\":\"doc1\",\"archivedAt\":\"2025-09-11\"}]"
        );

        // Stub NotificationService
        stubService(
                "/api/notifications",
                "{ \"clientId\": { \"equalTo\": \"123\" } }",
                "[{\"id\":\"notif1\",\"documentId\":\"doc1\",\"channel\":\"EMAIL\",\"status\":\"SENT\"}]"
        );
    }

    @Test
    void shouldReturnAggregatedDocumentHistory() {
        webTestClient.get()
                .uri("/api/clients/123/document-history")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.clientId").isEqualTo("123")
                .jsonPath("$.data.documents[0].id").isEqualTo("doc1")
                .jsonPath("$.data.documents[0].status").isEqualTo("OK")
                .jsonPath("$.data.archives[0].id").isEqualTo("arch1")
                .jsonPath("$.data.notifications[0].channel").isEqualTo("EMAIL");
    }

    @Test
    void shouldReturnServiceUnavailableWhenDocumentServiceFails() throws IOException, InterruptedException {
        // Stub DocumentService pour renvoyer 503
        String stubUrl = "http://" + wiremock.getHost() + ":" + wiremock.getMappedPort(8080) + "/__admin/mappings";
        String body = """
                {
                  "request": {
                    "method": "GET",
                    "urlPath": "/api/documents",
                    "queryParameters": { "clientId": { "equalTo": "123" } }
                  },
                  "response": { "status": 503 }
                }
                """;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(stubUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        webTestClient.get()
                .uri("/api/clients/123/document-history")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.errorCode").isEqualTo("SERVICE_UNAVAILABLE");
    }*/
}
