package com.example.payment.domain.feature.impl;

import com.example.payment.domain.exception.AccountNotFoundException;
import com.example.payment.domain.feature.FindAccountById;
import com.example.payment.domain.model.Account;
import com.example.payment.domain.model.AccountRepository;
import com.example.payment.domain.model.Client;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.util.UUID;

@Named
@AllArgsConstructor
public class FindAccountByIdImpl implements FindAccountById {

  private final AccountRepository accountRepository;

  @Override
  public Mono<Account> handle(UUID id) {
    return accountRepository
        .findByClient(new Client(id))
        .switchIfEmpty(Mono.error(new AccountNotFoundException()));
  }
}
