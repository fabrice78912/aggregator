package com.example.aggregator.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableReactiveMethodSecurity
public class SecurityConfig {

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
