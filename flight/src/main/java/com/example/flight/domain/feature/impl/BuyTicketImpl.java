package com.example.flight.domain.feature.impl;

import com.example.flight.domain.feature.BuyTicket;
import com.example.flight.domain.model.Customer;
import com.example.flight.domain.model.Ticket;
import com.example.flight.domain.model.TicketsRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.util.UUID;

@Named
@AllArgsConstructor
public class BuyTicketImpl implements BuyTicket {

  private final TicketsRepository ticketsRepository;

  @Override
  public Mono<Ticket> handle(Customer customer, String from, String destination) {
    return Mono.just(createTicket(customer, from, destination))
        .flatMap(ticket -> ticketsRepository.save(ticket).thenReturn(ticket));
  }

  private Ticket createTicket(Customer customer, String from, String destination) {
    final var ticket = new Ticket();
    ticket.setId(UUID.randomUUID());
    ticket.setCustomer(customer);
    ticket.setFrom(from);
    ticket.setDestination(destination);
    return ticket;
  }
}
