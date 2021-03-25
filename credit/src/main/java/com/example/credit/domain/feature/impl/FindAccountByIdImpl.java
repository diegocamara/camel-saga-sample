package com.example.credit.domain.feature.impl;

import com.example.credit.domain.exception.AccountNotFoundException;
import com.example.credit.domain.feature.FindAccountById;
import com.example.credit.domain.model.Account;
import com.example.credit.domain.model.AccountRepository;
import com.example.credit.domain.model.Client;
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
