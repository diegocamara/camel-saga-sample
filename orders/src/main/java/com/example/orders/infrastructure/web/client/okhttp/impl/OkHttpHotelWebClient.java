package com.example.orders.infrastructure.web.client.okhttp.impl;

import com.example.orders.infrastructure.configuration.properties.WebClientProperties;
import com.example.orders.infrastructure.web.client.HotelWebClient;
import com.example.orders.infrastructure.web.client.okhttp.exception.HttpClientException;
import com.example.orders.infrastructure.web.model.BookingRequest;
import com.example.orders.infrastructure.web.model.BookingResponse;
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
public class OkHttpHotelWebClient implements HotelWebClient {

  private static final String TRANSACTION_REFERENCE_HEADER = "transaction-reference";
  private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private final OkHttpClient okHttpClient;
  private final ObjectMapper objectMapper;
  private final WebClientProperties webClientProperties;

  @Override
  @SneakyThrows
  public BookingResponse createBooking(BookingRequest bookingRequest, UUID transactionId) {
    final var hotelProperties = webClientProperties.getHotel();
    final var url = hotelProperties.getBaseUrl() + "/booking";
    final var requestBody =
        RequestBody.create(objectMapper.writeValueAsString(bookingRequest), JSON);
    final var request =
        new Request.Builder()
            .url(url)
            .header(TRANSACTION_REFERENCE_HEADER, transactionId.toString())
            .post(requestBody)
            .build();
    try (final var response = okHttpClient.newCall(request).execute()) {

      if (response.isSuccessful()) {
        final var bookingResponse =
            objectMapper.readValue(
                Objects.requireNonNull(response.body()).bytes(), BookingResponse.class);
        bookingResponse.setTransactionId(transactionId);
        return bookingResponse;
      }

      throw new HttpClientException(response);
    }
  }

  @Override
  @SneakyThrows
  public void cancelBooking(UUID bookingId) {
    final var hotelProperties = webClientProperties.getHotel();
    final var url = hotelProperties.getBaseUrl() + "/booking/" + bookingId.toString();
    final var request = new Request.Builder().url(url).delete().build();
    try (final var response = okHttpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new HttpClientException(response);
      }
    }
  }
}
