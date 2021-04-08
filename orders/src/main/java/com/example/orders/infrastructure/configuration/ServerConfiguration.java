package com.example.orders.infrastructure.configuration;

import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfiguration {
  @Bean
  public UndertowServletWebServerFactory undertowServletWebServerFactory() {
    return new UndertowServletWebServerFactory();
  }
}
