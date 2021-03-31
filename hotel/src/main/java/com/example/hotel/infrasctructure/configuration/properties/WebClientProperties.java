package com.example.hotel.infrasctructure.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "web.client")
public class WebClientProperties {

  private Client payments;

  @Data
  public static class Client {
    private String baseUrl;
  }
}
