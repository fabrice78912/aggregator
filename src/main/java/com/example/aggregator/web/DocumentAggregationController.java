package com.example.aggregator.web;

import com.example.aggregator.model.DocumentHistory;
import com.example.aggregator.service.DocumentAggregationService;
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

    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    @GetMapping("/{id}/document-history")
    public Mono<DocumentHistory> getDocumentHistory(@PathVariable String id) {
        return service.getClientHistory(id);
    }
}
