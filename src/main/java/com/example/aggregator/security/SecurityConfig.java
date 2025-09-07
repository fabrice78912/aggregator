package com.example.aggregator.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableReactiveMethodSecurity
public class SecurityConfig {

  /**
   * Configure la sécurité de l’application WebFlux pour tous les endpoints.
   *
   * <p>Cette méthode définit :
   *
   * <ul>
   *   <li>Les endpoints publics (accessibles sans authentification) :
   *       <ul>
   *         <li>/api/public/**
   *         <li>/actuator/health
   *         <li>/swagger-ui/** et /swagger-ui.html
   *         <li>/v3/api-docs/** et /swagger-resources/**
   *         <li>/login et /webjars/**
   *       </ul>
   *   <li>Les endpoints protégés nécessitant une authentification :
   *       <ul>
   *         <li>/api/clients/**
   *         <li>/api/rapports/**
   *         <li>/api/documents/**
   *         <li>/api/archive/**
   *         <li>/api/notifications/**
   *       </ul>
   *   <li>Le CSRF est désactivé car l'application est principalement une API REST réactive.
   *   <li>OAuth2 Resource Server est configuré pour valider les JWT émis par Keycloak, avec un
   *       convertisseur personnalisé (KeycloakJwtReactiveConverter) pour extraire les rôles et
   *       autorités.
   *   <li>En cas d’accès non autorisé, la réponse HTTP renverra un code 401 Unauthorized.
   * </ul>
   *
   * <p>Exemple d'utilisation :
   *
   * <pre>
   *   curl -H "Authorization: Bearer &lt;token_jwt&gt;" http://localhost:8077/api/clients/1/document-history
   * </pre>
   *
   * @param http l'objet ServerHttpSecurity utilisé pour configurer la sécurité WebFlux
   * @return SecurityWebFilterChain configuré pour gérer l'authentification et l'autorisation
   */
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    String[] publicEndpoints = {
      "/api/public/**",
      "/actuator/health",
      "/swagger-ui/**",
      "/swagger-ui.html",
      "/v3/api-docs/**",
      "/swagger-resources/**",
      "/login",
      "/webjars/**"
    };

    String[] protectedEndpoints = {
      "/api/clients/**",
      "/api/rapports/**",
      "/api/documents/**",
      "/api/archive/**",
      "/api/notifications/**"
    };

    http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(
            exchanges ->
                exchanges
                    .pathMatchers(publicEndpoints)
                    .permitAll()
                    .pathMatchers(protectedEndpoints)
                    .authenticated()
                    .anyExchange()
                    .denyAll())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(
                    jwt ->
                        jwt.jwtAuthenticationConverter(
                            new com.example.aggregator.security.keycloak
                                .KeycloakJwtReactiveConverter())))
        .exceptionHandling(
            exceptions ->
                exceptions.authenticationEntryPoint(
                    (exchange, ex) -> {
                      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                      return exchange.getResponse().setComplete();
                    }));

    return http.build();
  }
}
