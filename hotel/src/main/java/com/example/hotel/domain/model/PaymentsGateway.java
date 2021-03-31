package com.example.hotel.domain.model;

import reactor.core.publisher.Mono;

public interface PaymentsGateway {
  Mono<Account> debit(AccountDebitInput accountDebitInput);

  Mono<Account> credit(AccountCreditInput accountCreditInput);
}
