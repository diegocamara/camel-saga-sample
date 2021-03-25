package com.example.payment.domain.feature.impl;

import com.example.payment.domain.feature.CreateAccount;
import com.example.payment.domain.model.Account;
import com.example.payment.domain.model.AccountRepository;
import com.example.payment.domain.model.Client;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.math.BigDecimal;

@Named
@AllArgsConstructor
public class CreateAccountImpl implements CreateAccount {

  private final AccountRepository accountRepository;

  @Override
  public Mono<Account> handle(Client client, BigDecimal limit) {
    return createAccount(client, limit)
        .flatMap(credit -> accountRepository.save(credit).thenReturn(credit));
  }

  private Mono<Account> createAccount(Client client, BigDecimal limit) {
    final var credit = new Account();
    credit.setClient(client);
    credit.setMaxLimit(limit);
    return Mono.just(credit);
  }
}
