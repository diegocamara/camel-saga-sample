package com.example.flight.domain.feature.impl;

import com.example.flight.domain.exception.TicketNotFoundException;
import com.example.flight.domain.feature.FindTicketById;
import com.example.flight.domain.model.Ticket;
import com.example.flight.domain.model.TicketsRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.util.UUID;

@Named
@AllArgsConstructor
public class FindTicketByIdImpl implements FindTicketById {

  private final TicketsRepository ticketsRepository;

  @Override
  public Mono<Ticket> handle(UUID id) {
    return ticketsRepository
        .findTicketById(id)
        .switchIfEmpty(Mono.error(new TicketNotFoundException()));
  }
}
