package com.example.flight.domain.feature.impl;

import com.example.flight.domain.exception.TicketCustomerRelationshipNotFoundException;
import com.example.flight.domain.feature.FindTicketCustomerRelationshipById;
import com.example.flight.domain.model.TicketCustomerRelationship;
import com.example.flight.domain.model.TicketsCustomerRelationshipRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;
import java.util.UUID;

@Named
@AllArgsConstructor
public class FindTicketCustomerRelationshipByIdImpl implements FindTicketCustomerRelationshipById {

  private final TicketsCustomerRelationshipRepository ticketsCustomerRelationshipRepository;

  @Override
  public Mono<TicketCustomerRelationship> handle(UUID ticketId, UUID customerId) {
    return ticketsCustomerRelationshipRepository
        .findById(ticketId, customerId)
        .switchIfEmpty(Mono.error(new TicketCustomerRelationshipNotFoundException()));
  }
}
