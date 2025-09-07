package com.example.aggregator.security.keycloak;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

@Slf4j
public class KeycloakJwtReactiveConverter
    implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

  private static final Logger logger = LoggerFactory.getLogger(KeycloakJwtReactiveConverter.class);

  /**
   * Convertit un JWT Keycloak en un token d'authentification réactif (AbstractAuthenticationToken)
   * utilisable par Spring Security WebFlux.
   *
   * @param jwt Le JWT reçu lors de l'authentification, contenant les informations de l'utilisateur
   *     et ses rôles.
   * @return Un Mono encapsulant un UsernamePasswordAuthenticationToken contenant : - le nom de
   *     l'utilisateur (subject du JWT) - un mot de passe fictif ("n/a", non utilisé) - les
   *     autorités/roles extraits du JWT
   */
  @Override
  public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {

    // === LOG pour debug avec SLF4J ===
    logger.info("JWT Claims: {}", String.valueOf(jwt.getClaims()));
    logger.info("resource_access: {}", String.valueOf(jwt.getClaim("resource_access")));

    Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
    List<String> roles = List.of();

    if (resourceAccess != null && resourceAccess.containsKey("account")) {
      Map<String, Object> account = (Map<String, Object>) resourceAccess.get("account");
      roles = (List<String>) account.getOrDefault("roles", List.of());
    }

    Collection<GrantedAuthority> authorities =
        roles.stream()
            .map(role -> (GrantedAuthority) () -> role)
            .peek(a -> System.out.println("GrantedAuthority: " + a.getAuthority()))
            .collect(Collectors.toList());

    return Mono.just(new UsernamePasswordAuthenticationToken(jwt.getSubject(), "n/a", authorities));
  }
}
