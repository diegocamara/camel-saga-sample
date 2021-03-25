package com.example.payment.domain.feature.impl;

import com.example.payment.domain.feature.AccountDebit;
import com.example.payment.domain.feature.FindAccountById;
import com.example.payment.domain.model.Account;
import com.example.payment.domain.model.AccountRepository;
import com.example.payment.domain.model.Client;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.math.BigDecimal;

@Named
@AllArgsConstructor
public class AccountDebitImpl implements AccountDebit {

  private final AccountRepository accountRepository;
  private final FindAccountById findAccountById;

  @Override
  public Mono<Account> handle(Client client, BigDecimal amount) {
    return findAccountById
        .handle(client.getId())
        .flatMap(
            credit -> {
              credit.debit(amount);
              return accountRepository.update(credit).thenReturn(credit);
            });
  }
}
