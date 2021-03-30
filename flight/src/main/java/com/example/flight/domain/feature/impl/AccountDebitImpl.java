package com.example.flight.domain.feature.impl;

import com.example.flight.domain.feature.AccountDebit;
import com.example.flight.domain.model.Account;
import com.example.flight.domain.model.AccountDebitInput;
import com.example.flight.domain.model.PaymentsGateway;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;

@Named
@AllArgsConstructor
public class AccountDebitImpl implements AccountDebit {

  private final PaymentsGateway paymentsGateway;

  @Override
  public Mono<Account> handle(AccountDebitInput accountDebitInput) {
    return paymentsGateway.debit(accountDebitInput);
  }
}
