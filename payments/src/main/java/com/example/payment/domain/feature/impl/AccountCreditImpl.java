package com.example.payment.domain.feature.impl;

import com.example.payment.domain.feature.AccountCredit;
import com.example.payment.domain.feature.FindAccountById;
import com.example.payment.domain.model.Account;
import com.example.payment.domain.model.AccountRepository;
import com.example.payment.domain.model.Customer;
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
  public Mono<Account> handle(Customer customer, BigDecimal amount) {
    return findAccountById
        .handle(customer.getId())
        .flatMap(
            account -> {
              account.credit(amount);
              return accountRepository.update(account).thenReturn(account);
            });
  }
}
