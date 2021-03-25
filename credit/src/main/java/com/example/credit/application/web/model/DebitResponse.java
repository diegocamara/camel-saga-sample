package com.example.credit.application.web.model;

import com.example.credit.domain.model.Account;
import com.example.credit.infrasctructure.repository.table.OperationTable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DebitResponse {
  private UUID client;
  private BigDecimal used;
  private UUID transactionId;

  public DebitResponse(Account account, UUID transactionId) {
    this.client = account.getClient().getId();
    this.used = account.getUsed();
    this.transactionId = transactionId;
  }

  public DebitResponse(OperationTable operationTable) {
    this.client = operationTable.getAccount().getId();
    this.used = operationTable.getAccount().getUsed();
    this.transactionId = operationTable.getId();
  }
}