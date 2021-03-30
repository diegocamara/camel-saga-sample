package com.example.flight.domain.feature;

import com.example.flight.domain.model.Account;
import com.example.flight.domain.model.AccountDebitInput;
import reactor.core.publisher.Mono;

public interface AccountDebit {
  Mono<Account> handle(AccountDebitInput accountDebitInput);
}
