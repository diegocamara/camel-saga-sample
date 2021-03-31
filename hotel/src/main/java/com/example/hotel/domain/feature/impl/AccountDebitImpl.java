package com.example.hotel.domain.feature.impl;

import com.example.hotel.domain.feature.AccountDebit;
import com.example.hotel.domain.model.Account;
import com.example.hotel.domain.model.AccountDebitInput;
import com.example.hotel.domain.model.PaymentsGateway;
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
