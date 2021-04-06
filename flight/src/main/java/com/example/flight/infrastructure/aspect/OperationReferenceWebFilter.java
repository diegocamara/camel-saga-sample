package com.example.flight.infrastructure.aspect;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

// @Component
public class OperationReferenceWebFilter implements WebFilter {

  public static String OPERATION_REFERENCE_HEADER = "operation-reference";

  @Override
  public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
    return webFilterChain
        .filter(serverWebExchange)
        .contextWrite(
            context ->
                context.put(
                    OPERATION_REFERENCE_HEADER,
                    headerValue(serverWebExchange, OPERATION_REFERENCE_HEADER)));
  }

  private String headerValue(ServerWebExchange serverWebExchange, String header) {
    return serverWebExchange.getRequest().getHeaders().toSingleValueMap().get(header);
  }
}
