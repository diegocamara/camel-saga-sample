package com.example.hotel.infrasctructure.gateway.model;

import com.example.hotel.domain.model.AccountDebitInput;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DebitRequest {
  private BigDecimal amount;

  public DebitRequest(AccountDebitInput accountDebitInput) {
    this.amount = accountDebitInput.getAmount();
  }
}
