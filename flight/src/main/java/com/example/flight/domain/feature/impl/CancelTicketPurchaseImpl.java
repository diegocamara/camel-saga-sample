package com.example.flight.domain.feature.impl;

import com.example.flight.domain.feature.CancelTicketPurchase;
import com.example.flight.domain.feature.FindTicketCustomerRelationshipById;
import com.example.flight.domain.model.CancelTicketPurchaseInput;
import com.example.flight.domain.model.TicketsCustomerRelationshipRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.inject.Named;

@Named
@AllArgsConstructor
public class CancelTicketPurchaseImpl implements CancelTicketPurchase {

  private final FindTicketCustomerRelationshipById findTicketCustomerRelationshipById;
  private final TicketsCustomerRelationshipRepository ticketsCustomerRelationshipRepository;

  @Override
  public Mono<Void> handle(CancelTicketPurchaseInput cancelTicketPurchaseInput) {
    return findTicketCustomerRelationshipById
        .handle(cancelTicketPurchaseInput.getTicketId(), cancelTicketPurchaseInput.getCustomerId())
        .flatMap(ticketsCustomerRelationshipRepository::delete);
  }
}
