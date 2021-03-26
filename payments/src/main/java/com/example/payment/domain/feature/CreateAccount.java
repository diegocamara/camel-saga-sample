package com.example.payment.domain.feature;

import com.example.payment.domain.model.Account;
import com.example.payment.domain.model.Customer;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CreateAccount {
  Mono<Account> handle(Customer customer, BigDecimal limit);
}
