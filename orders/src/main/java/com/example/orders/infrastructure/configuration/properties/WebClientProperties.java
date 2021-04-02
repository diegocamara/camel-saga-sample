package com.example.orders.infrastructure.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "web.client")
public class WebClientProperties {

  private ClientProperties flight;
  private ClientProperties hotel;

  @Data
  public static class ClientProperties {
    private String baseUrl;
  }
}
