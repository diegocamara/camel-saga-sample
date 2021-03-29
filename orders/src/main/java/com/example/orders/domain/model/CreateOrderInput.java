package com.example.orders.domain.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderInput {
  private UUID customerId;
  private List<Item> items;
}
