package com.example.flight.domain.feature;

import com.example.flight.domain.model.Ticket;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FindTicketById {
  Mono<Ticket> handle(UUID id);
}
