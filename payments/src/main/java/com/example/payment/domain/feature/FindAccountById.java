package com.example.payment.domain.feature;

import com.example.payment.domain.model.Account;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FindAccountById {
  Mono<Account> handle(UUID id);
}
