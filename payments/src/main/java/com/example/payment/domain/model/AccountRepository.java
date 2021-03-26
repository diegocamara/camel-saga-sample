package com.example.payment.domain.model;

import reactor.core.publisher.Mono;

public interface AccountRepository {
  Mono<Void> save(Account account);

  Mono<Void> update(Account account);

  Mono<Account> findByCustomer(Customer customer);
}
