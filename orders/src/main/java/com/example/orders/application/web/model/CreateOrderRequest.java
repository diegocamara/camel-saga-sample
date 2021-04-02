package com.example.orders.application.web.model;

import com.example.orders.domain.model.CreateOrderInput;
import com.example.orders.domain.model.Item;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequest {

  private UUID customerId;
  private List<Item> items;

  public CreateOrderInput toCreateOrderInput() {
    return new CreateOrderInput(this.customerId, this.items);
  }
}
