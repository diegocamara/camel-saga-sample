package com.example.orders.application.web.model;

import com.example.orders.domain.model.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderResponse {
  private UUID id;

  public OrderResponse(Order order) {
    this.id = order.getId();
  }
}
