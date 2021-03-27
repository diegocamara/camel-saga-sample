package com.example.flight.domain.feature;

import com.example.flight.domain.model.TicketCustomerRelationship;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FindTicketCustomerRelationshipById {
  Mono<TicketCustomerRelationship> handle(UUID ticketId, UUID customerId);
}
