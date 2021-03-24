package com.example.credit.application.web.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConsumeRequest {
  private BigDecimal amount;
}
