package com.example.flight.domain.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Ticket {
  private UUID id;
  private BigDecimal price;
  private String from;
  private String destination;
}
