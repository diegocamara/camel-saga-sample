package com.example.flight.domain.feature;

import com.example.flight.domain.model.Customer;
import com.example.flight.domain.model.Ticket;
import reactor.core.publisher.Mono;

public interface BuyTicket {
  Mono<Ticket> handle(Customer customer, String from, String destination);
}
