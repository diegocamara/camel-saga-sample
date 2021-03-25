package com.example.credit.domain.feature;

import com.example.credit.domain.model.Account;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FindAccountById {
  Mono<Account> handle(UUID id);
}
