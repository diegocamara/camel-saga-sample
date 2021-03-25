package com.example.credit.domain.feature.impl;

import com.example.credit.domain.feature.AccountCredit;
import com.example.credit.domain.feature.FindAccountById;
import com.example.credit.domain.model.Account;
import com.example.credit.domain.model.AccountRepository;
import com.example.credit.domain.model.Client;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.math.BigDecimal;

@Named
@AllArgsConstructor
public class AccountCreditImpl implements AccountCredit {

  private final AccountRepository accountRepository;
  private final FindAccountById findAccountById;

  @Override
  public Mono<Account> handle(Client client, BigDecimal amount) {
    return findAccountById
        .handle(client.getId())
        .flatMap(
            account -> {
              account.credit(amount);
              return accountRepository.update(account).thenReturn(account);
            });
  }
}
