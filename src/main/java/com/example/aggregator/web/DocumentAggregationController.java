package com.example.aggregator.web;

import com.example.aggregator.model.DocumentHistory;
import com.example.aggregator.service.DocumentAggregationService;
import com.example.common_lib.model.response.ApiResponse1;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clients")
public class DocumentAggregationController {

  private final DocumentAggregationService service;

  public DocumentAggregationController(DocumentAggregationService service) {
    this.service = service;
  }

  /**
   * Récupère l'historique des documents pour un client. Si la récupération échoue (ex:
   * DocumentService indisponible), renvoie automatiquement le HTTP status correspondant via
   * ResponseStatusException.
   *
   * @param id l'identifiant du client
   * @return Mono encapsulant ApiResponse1<DocumentHistory>
   */
  @GetMapping("/{id}/document-history")
  @PreAuthorize("hasAuthority('agent')")
  public Mono<ResponseEntity<ApiResponse1<DocumentHistory>>> getDocumentHistory(
      @PathVariable String id) {
    return service
        .getClientHistory(id)
        .flatMap(
            resp -> {
              // Si ApiResponse1 indique un échec
              if (!resp.isSuccess()) {
                return Mono.just(ResponseEntity.status(resp.getStatusCode()).body(resp));
              }
              // Sinon succès
              return Mono.just(ResponseEntity.ok(resp));
            });
  }
}
