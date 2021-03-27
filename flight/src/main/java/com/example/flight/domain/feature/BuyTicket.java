package com.example.flight.domain.feature;

import com.example.flight.domain.model.BuyTicketInput;
import com.example.flight.domain.model.TicketCustomerRelationship;
import reactor.core.publisher.Mono;

public interface BuyTicket {
  Mono<TicketCustomerRelationship> handle(BuyTicketInput buyTicketInput);
}
