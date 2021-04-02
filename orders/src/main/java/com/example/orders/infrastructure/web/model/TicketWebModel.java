package com.example.orders.infrastructure.web.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TicketWebModel {
  private UUID id;
  private BigDecimal price;
  private String from;
  private String destination;
}
