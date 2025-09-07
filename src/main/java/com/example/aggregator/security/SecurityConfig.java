package com.example.aggregator.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // Liste des chemins protégés par JWT
        String[] protectedEndpoints = {
                "/api/clients/**",
                "/api/rapports/**",
                "/api/documents/**",    // tu peux ajouter d'autres endpoints sensibles ici
                "/api/archive/**",
                "/api/notifications/**"
        };

        // Liste des chemins publics (accessibles sans authentification)
        String[] publicEndpoints = {
                "/api/public/**",
                "/actuator/health",
                "/swagger-ui/**",
                "/v3/api-docs/**"
        };

        return http
                .csrf().disable()
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(protectedEndpoints).authenticated()  // JWT requis
                        .pathMatchers(publicEndpoints).permitAll()        // accessible sans auth
                        .anyExchange().denyAll()                           // tout le reste est refusé
                )
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt)
                .build();
    }
}

