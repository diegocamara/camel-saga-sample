package com.example.orders;

import com.example.orders.application.web.model.CreateOrderRequest;
import com.example.orders.domain.model.Item;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.UUID;

class OrdersApplicationTests extends IntegrationTest {

  private final UUID customerId = UUID.randomUUID();

  @Test
  void shouldCreateCustomerOrder() {

    final var createOrderRequest = new CreateOrderRequest();
    createOrderRequest.setCustomerId(customerId);
    createOrderRequest.setItems(Arrays.asList(Item.BUY_FLIGHT_TICKET, Item.BOOKING_HOTEL));

    final var createOrderRequestSpecification =
        RestAssured.given()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(writeValueAsString(createOrderRequest));

    final var createOrderResponse = createOrderRequestSpecification.post("/orders");

    createOrderResponse.body();
  }
}
