package com.example.hotel.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Bedroom {
  private final UUID id;
  private String description;
  private BigDecimal price;
}
