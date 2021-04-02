package com.example.orders.application.web.controller;

import com.example.orders.application.web.model.CreateOrderRequest;
import com.example.orders.application.web.model.OrderResponse;
import com.example.orders.domain.feature.CreateOrder;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

  private final CreateOrder createOrder;

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(
      @RequestBody CreateOrderRequest createOrderRequest) {
    final var order = createOrder.handle(createOrderRequest.toCreateOrderInput());
    final var uri =
        UriComponentsBuilder.fromUriString("/orders/{orderId}")
            .buildAndExpand(order.getId().toString())
            .toUri();
    return ResponseEntity.created(uri).build();
  }
}
