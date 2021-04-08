package com.example.orders.infrastructure.web.client.okhttp.impl;

import com.example.orders.infrastructure.configuration.properties.WebClientProperties;
import com.example.orders.infrastructure.web.client.FlightWebClient;
import com.example.orders.infrastructure.web.client.okhttp.exception.HttpClientException;
import com.example.orders.infrastructure.web.model.BuyTicketRequest;
import com.example.orders.infrastructure.web.model.BuyTicketResponse;
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

  private static final String OPERATION_REFERENCE_HEADER = "operation-reference";
  private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private final OkHttpClient okHttpClient;
  private final ObjectMapper objectMapper;
  private final WebClientProperties webClientProperties;

  @Override
  @SneakyThrows
  public BuyTicketResponse buyTicket(BuyTicketRequest buyTicketRequest, UUID operationReference) {
    final var flightProperties = webClientProperties.getFlight();
    final var url = flightProperties.getBaseUrl() + "/tickets";
    final var requestBody =
        RequestBody.create(objectMapper.writeValueAsString(buyTicketRequest), JSON);
    final var request =
        new Request.Builder()
            .url(url)
            .header(OPERATION_REFERENCE_HEADER, operationReference.toString())
            .post(requestBody)
            .build();
    try (final var response = okHttpClient.newCall(request).execute()) {
      if (response.isSuccessful()) {
        final var buyTicketResponse =
            objectMapper.readValue(
                Objects.requireNonNull(response.body()).bytes(), BuyTicketResponse.class);
        buyTicketResponse.setOperationReference(operationReference);
        return buyTicketResponse;
      }
      throw new HttpClientException(response);
    }
  }

  @Override
  @SneakyThrows
  public void cancelTicket(UUID operationReference) {
    final var flightProperties = webClientProperties.getFlight();
    final var url = flightProperties.getBaseUrl() + "/tickets";
    final var request =
        new Request.Builder()
            .url(url)
            .header(OPERATION_REFERENCE_HEADER, operationReference.toString())
            .delete()
            .build();
    try (final var response = okHttpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new HttpClientException(response);
      }
    }
  }
}
