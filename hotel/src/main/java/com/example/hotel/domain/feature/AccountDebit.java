package com.example.hotel.domain.feature;

import com.example.hotel.domain.model.Account;
import com.example.hotel.domain.model.AccountDebitInput;
import reactor.core.publisher.Mono;

public interface AccountDebit {
  Mono<Account> handle(AccountDebitInput accountDebitInput);
}
