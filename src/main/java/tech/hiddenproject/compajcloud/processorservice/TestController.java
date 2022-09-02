package tech.hiddenproject.compajcloud.processorservice;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @PreAuthorize("hasAuthority('SCOPE_compaj-aud')")
  @GetMapping("/hello")
  public String hello(@AuthenticationPrincipal Jwt principal) {
    return "Hello, user: " + principal.getId();
  }

  @GetMapping("/open")
  public String open() {
    return "No role check";
  }

}
