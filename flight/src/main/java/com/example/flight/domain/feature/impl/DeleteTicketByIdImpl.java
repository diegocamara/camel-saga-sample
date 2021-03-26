package com.example.flight.domain.feature.impl;

import com.example.flight.domain.feature.DeleteTicketById;
import com.example.flight.domain.feature.FindTicketById;
import com.example.flight.domain.model.TicketsRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.util.UUID;

@Named
@AllArgsConstructor
public class DeleteTicketByIdImpl implements DeleteTicketById {

  private final TicketsRepository ticketsRepository;
  private final FindTicketById findTicketById;

  @Override
  public Mono<Void> handle(UUID id) {
    return findTicketById.handle(id).flatMap(ticketsRepository::delete);
  }
}
