package com.example.payment.application.web.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditRequest {
  private BigDecimal amount;
}
