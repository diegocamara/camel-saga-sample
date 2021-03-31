package com.example.hotel.infrasctructure.gateway.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreditResponse {
  private UUID customer;
  private BigDecimal used;
  private UUID transactionId;
}
