package com.example.flight.domain.feature;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DeleteTicketById {
  Mono<Void> handle(UUID id);
}
