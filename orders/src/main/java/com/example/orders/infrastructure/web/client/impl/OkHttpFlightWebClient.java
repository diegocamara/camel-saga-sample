package com.example.orders.infrastructure.web.client.impl;

import com.example.orders.infrastructure.configuration.properties.WebClientProperties;
import com.example.orders.infrastructure.web.client.FlightWebClient;
import com.example.orders.infrastructure.web.model.BuyTicketRequest;
import com.example.orders.infrastructure.web.model.BuyTicketResponse;
import com.example.orders.infrastructure.web.model.CancelTicketPurchaseRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OkHttpFlightWebClient implements FlightWebClient {

  private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private final OkHttpClient okHttpClient;
  private final ObjectMapper objectMapper;
  private final WebClientProperties webClientProperties;

  @Override
  @SneakyThrows
  public BuyTicketResponse buyTicket(BuyTicketRequest buyTicketRequest, UUID transactionId) {
    final var flightProperties = webClientProperties.getFlight();
    final var url = flightProperties.getBaseUrl() + "/tickets";
    final var requestBody =
        RequestBody.create(objectMapper.writeValueAsString(buyTicketRequest), JSON);
    final var request = new Request.Builder().url(url).post(requestBody).build();
    try (final var response = okHttpClient.newCall(request).execute()) {
      return objectMapper.readValue(
          Objects.requireNonNull(response.body()).bytes(), BuyTicketResponse.class);
    }
  }

  @Override
  public void cancelTicket(CancelTicketPurchaseRequest cancelTicketPurchaseRequest) {}
}
