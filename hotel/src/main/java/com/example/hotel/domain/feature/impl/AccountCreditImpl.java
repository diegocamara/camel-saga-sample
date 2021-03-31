package com.example.hotel.domain.feature.impl;

import com.example.hotel.domain.feature.AccountCredit;
import com.example.hotel.domain.model.Account;
import com.example.hotel.domain.model.AccountCreditInput;
import com.example.hotel.domain.model.PaymentsGateway;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;

@Named
@AllArgsConstructor
public class AccountCreditImpl implements AccountCredit {

  private final PaymentsGateway paymentsGateway;

  @Override
  public Mono<Account> handle(AccountCreditInput accountCreditInput) {
    return paymentsGateway.credit(accountCreditInput);
  }
}
