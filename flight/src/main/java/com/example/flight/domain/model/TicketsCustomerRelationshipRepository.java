package com.example.flight.domain.model;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TicketsCustomerRelationshipRepository {
  Mono<Void> save(TicketCustomerRelationship ticketCustomerRelationship);

  Mono<TicketCustomerRelationship> findById(UUID ticketId, UUID customerId);

  Mono<Void> delete(TicketCustomerRelationship ticketCustomerRelationship);
}
