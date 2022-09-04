package tech.hiddenproject.compajcloud.processorservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
   return http
       .csrf()
       .disable()
       .authorizeRequests()
       .anyRequest()
       .authenticated()
       .and()
       .oauth2ResourceServer()
       .jwt()
       .and()
       .and().build();
  }

}
