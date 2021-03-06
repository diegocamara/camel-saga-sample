package com.example.payment.domain.model;

import com.example.payment.domain.exception.MaximumLimitReached;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Account {
  private Customer customer;
  private BigDecimal used;
  private BigDecimal maxLimit;

  public void credit(BigDecimal amount) {
    this.used = this.used.subtract(amount);
  }

  public void debit(BigDecimal amount) {
    final var newUsed = this.used.add(amount);
    if (newUsed.compareTo(this.maxLimit) > 0) {
      throw new MaximumLimitReached();
    }
    this.used = newUsed;
  }
}
