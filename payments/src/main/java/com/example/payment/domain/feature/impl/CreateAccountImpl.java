package com.example.payment.domain.feature.impl;

import com.example.payment.domain.feature.CreateAccount;
import com.example.payment.domain.model.Account;
import com.example.payment.domain.model.AccountRepository;
import com.example.payment.domain.model.Customer;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.math.BigDecimal;

@Named
@AllArgsConstructor
public class CreateAccountImpl implements CreateAccount {

  private final AccountRepository accountRepository;

  @Override
  public Mono<Account> handle(Customer customer, BigDecimal limit) {
    return createAccount(customer, limit)
        .flatMap(credit -> accountRepository.save(credit).thenReturn(credit));
  }

  private Mono<Account> createAccount(Customer customer, BigDecimal limit) {
    final var account = new Account();
    account.setCustomer(customer);
    account.setMaxLimit(limit);
    return Mono.just(account);
  }
}
