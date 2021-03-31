package com.example.flight.domain.feature.impl;

import com.example.flight.domain.feature.AccountDebit;
import com.example.flight.domain.feature.BuyTicket;
import com.example.flight.domain.feature.FindTicketById;
import com.example.flight.domain.model.*;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;

@Named
@AllArgsConstructor
public class BuyTicketImpl implements BuyTicket {

  private final FindTicketById findTicketById;
  private final AccountDebit accountDebit;
  private final TicketsCustomerRelationshipRepository ticketsCustomerRelationshipRepository;

  @Override
  public Mono<TicketCustomerRelationship> handle(BuyTicketInput buyTicketInput) {
    return findTicketById
        .handle(buyTicketInput.getTicketId())
        .flatMap(
            ticket -> {
              final var ticketCustomerRelationship =
                  ticketCustomerRelationship(buyTicketInput, ticket);
              return ticketsCustomerRelationshipRepository
                  .save(ticketCustomerRelationship)
                  .then(
                      accountDebit.handle(
                          new AccountDebitInput(
                              ticketCustomerRelationship.getCustomer().getId(),
                              ticketCustomerRelationship.getTicket().getPrice())))
                  .thenReturn(ticketCustomerRelationship);
            });
  }

  private TicketCustomerRelationship ticketCustomerRelationship(
      BuyTicketInput buyTicketInput, Ticket ticket) {
    return new TicketCustomerRelationship(ticket, new Customer(buyTicketInput.getCustomerId()));
  }
}
