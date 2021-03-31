package com.example.flight.domain.feature;

import com.example.flight.domain.model.Account;
import com.example.flight.domain.model.AccountCreditInput;
import reactor.core.publisher.Mono;

public interface AccountCredit {
  Mono<Account> handle(AccountCreditInput accountCreditInput);
}
