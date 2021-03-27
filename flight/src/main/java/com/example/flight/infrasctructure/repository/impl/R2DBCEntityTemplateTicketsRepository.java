package com.example.flight.infrasctructure.repository.impl;

import com.example.flight.domain.model.Ticket;
import com.example.flight.domain.model.TicketsRepository;
import com.example.flight.infrasctructure.repository.reactive.ReactiveTicketsRepository;
import com.example.flight.infrasctructure.repository.table.TicketTable;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class R2DBCEntityTemplateTicketsRepository implements TicketsRepository {

  private final ReactiveTicketsRepository reactiveTicketsRepository;
  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  @Override
  public Mono<Void> save(Ticket ticket) {
    return r2dbcEntityTemplate.insert(new TicketTable(ticket)).then();
  }

  @Override
  public Mono<Ticket> findTicketById(UUID id) {
    return reactiveTicketsRepository.findById(id).map(this::ticket);
  }

  @Override
  public Mono<Void> delete(Ticket ticket) {
    return reactiveTicketsRepository.deleteById(ticket.getId());
  }

  private Ticket ticket(TicketTable ticketTable) {
    final var ticket = new Ticket();
    ticket.setId(ticketTable.getId());
    ticket.setFrom(ticketTable.getFrom());
    ticket.setDestination(ticketTable.getDestination());
    return ticket;
  }
}
