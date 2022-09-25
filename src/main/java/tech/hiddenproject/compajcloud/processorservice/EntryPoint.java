package tech.hiddenproject.compajcloud.processorservice;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Danila Rassokhin
 */
public class EntryPoint implements ServerAuthenticationEntryPoint {

  private final ServerWebExchangeMatcher failMatcher;

  private final RedirectServerAuthenticationEntryPoint redirectEntryPoint;

  private final HttpStatusServerEntryPoint httpStatusEntryPoint;

  public EntryPoint(final String redirectionUri, final String failurePath) {
    this.failMatcher = ServerWebExchangeMatchers.pathMatchers(failurePath);
    redirectEntryPoint = new RedirectServerAuthenticationEntryPoint(redirectionUri);
    httpStatusEntryPoint = new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED);
  }

  @Override
  public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
    return failMatcher.matches(exchange)
        .map(result -> result.isMatch() ? httpStatusEntryPoint : redirectEntryPoint)
        .flatMap(entryPoint -> entryPoint.commence(exchange, e));
  }

}
