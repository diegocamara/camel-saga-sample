package com.example.credit.application.web.model;

import com.example.credit.domain.model.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ConsumeResponse {
  private UUID client;
  private BigDecimal used;
  private UUID transactionId;

  public ConsumeResponse(Account account, UUID transactionId) {
    this.client = account.getClient().getId();
    this.used = account.getUsed();
    this.transactionId = transactionId;
  }
}
