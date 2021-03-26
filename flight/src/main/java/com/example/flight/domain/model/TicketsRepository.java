package com.example.flight.domain.model;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TicketsRepository {
  Mono<Void> save(Ticket ticket);

  Mono<Ticket> findTicketById(UUID id);

  Mono<Void> delete(Ticket ticket);
}
