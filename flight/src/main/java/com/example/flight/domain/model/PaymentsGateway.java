package com.example.flight.domain.model;

import reactor.core.publisher.Mono;

public interface PaymentsGateway {
  Mono<Account> debit(AccountDebitInput accountDebitInput);
}
