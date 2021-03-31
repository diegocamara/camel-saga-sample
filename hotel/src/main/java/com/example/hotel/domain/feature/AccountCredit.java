package com.example.hotel.domain.feature;

import com.example.hotel.domain.model.Account;
import com.example.hotel.domain.model.AccountCreditInput;
import reactor.core.publisher.Mono;

public interface AccountCredit {
  Mono<Account> handle(AccountCreditInput accountCreditInput);
}
