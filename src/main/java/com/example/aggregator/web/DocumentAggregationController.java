package com.example.aggregator.web;

import com.example.aggregator.model.DocumentHistory;
import com.example.aggregator.service.DocumentAggregationService;
import com.example.common_lib.model.response.ApiResponse1;
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

  /*  @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
  @GetMapping("/{id}/document-history")
  public Mono<DocumentHistory> getDocumentHistory(@PathVariable String id) {
    return service.getClientHistory1(id);
  }*/

  /**
   * Récupère l'historique des documents, archives et notifications d'un client. Accessible
   * uniquement si le token JWT contient le rôle/autorité "agent".
   *
   * @param id l'identifiant du client
   * @return Mono encapsulant ApiResponse1<DocumentHistory>
   */
  @PreAuthorize("hasAuthority('agent')")
  @GetMapping("/{id}/document-history")
  public Mono<ApiResponse1<DocumentHistory>> getDocumentHistory(@PathVariable String id) {
    return service.getClientHistory2(id);
  }
}
