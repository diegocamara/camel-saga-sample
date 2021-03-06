package com.example.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Order {
  private final UUID id;
  private final Customer customer;
  private final Items items;
  private final Timeline timeline;
  private final LocalDateTime date;
}
