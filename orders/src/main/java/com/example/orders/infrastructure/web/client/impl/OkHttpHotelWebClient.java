package com.example.orders.infrastructure.web.client.impl;

import com.example.orders.infrastructure.configuration.properties.WebClientProperties;
import com.example.orders.infrastructure.web.client.HotelWebClient;
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

  private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  private final OkHttpClient okHttpClient;
  private final ObjectMapper objectMapper;
  private final WebClientProperties webClientProperties;

  @Override
  @SneakyThrows
  public BookingResponse createBooking(BookingRequest bookingRequest) {
    final var hotelProperties = webClientProperties.getHotel();
    final var url = hotelProperties.getBaseUrl() + "/booking";
    final var requestBody =
        RequestBody.create(objectMapper.writeValueAsString(bookingRequest), JSON);
    final var request = new Request.Builder().url(url).post(requestBody).build();
    try (final var response = okHttpClient.newCall(request).execute()) {
      return objectMapper.readValue(
          Objects.requireNonNull(response.body()).bytes(), BookingResponse.class);
    }
  }

  @Override
  public void cancelBooking(UUID bookingId) {}
}
