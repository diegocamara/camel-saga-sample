package com.example.orders.infrastructure.configuration;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkHttpConfiguration {

  @Bean("flightHttpClient")
  public OkHttpClient flightHttpClient() {
    return null;
  }

  @Bean("hotelHttpClient")
  public OkHttpClient hotelHttpClient() {
    return null;
  }
}
