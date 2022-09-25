package tech.hiddenproject.compajcloud.processorservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import tech.hiddenproject.compajcloud.processorservice.EntryPoint;

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
    return http
        .csrf()
        .disable()
        .authorizeExchange()
        .pathMatchers("/file/**")
        .hasAuthority("SCOPE_processor")
        .anyExchange()
        .authenticated()
        .and()
        .oauth2ResourceServer()
        .jwt()
        .and()
        .authenticationEntryPoint(new EntryPoint("http://localhost:8080/login", "http://localhost:8080?error=auth"))
        .and()
        .build();
  }

}
