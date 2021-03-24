package com.example.credit.domain.feature;

import com.example.credit.domain.model.Account;
import com.example.credit.domain.model.Client;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CreateAccount {
  Mono<Account> handle(Client client, BigDecimal limit);
}
