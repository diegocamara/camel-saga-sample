package com.example.flight.infrasctructure.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DebitRequest {
  private BigDecimal amount;
}
