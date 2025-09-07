import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
@EnableWebFluxSecurity
public class TestSecurityConfig {
    @Bean
    public SecurityWebFilterChain testSecurity(ServerHttpSecurity http) {
        return http.csrf().disable()
                .authorizeExchange().anyExchange().permitAll()
                .and().build();
    }
}
