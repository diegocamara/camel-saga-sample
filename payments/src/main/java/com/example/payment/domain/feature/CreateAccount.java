package com.example.payment.domain.feature;

import com.example.payment.domain.model.Account;
import com.example.payment.domain.model.Client;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CreateAccount {
  Mono<Account> handle(Client client, BigDecimal limit);
}
