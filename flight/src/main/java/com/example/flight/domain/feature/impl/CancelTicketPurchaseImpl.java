package com.example.flight.domain.feature.impl;

import com.example.flight.domain.feature.AccountCredit;
import com.example.flight.domain.feature.CancelTicketPurchase;
import com.example.flight.domain.feature.FindTicketCustomerRelationshipById;
import com.example.flight.domain.model.AccountCreditInput;
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
  private final AccountCredit accountCredit;

  @Override
  public Mono<Void> handle(CancelTicketPurchaseInput cancelTicketPurchaseInput) {
    return findTicketCustomerRelationshipById
        .handle(cancelTicketPurchaseInput.getTicketId(), cancelTicketPurchaseInput.getCustomerId())
        .flatMap(
            ticketCustomerRelationship ->
                accountCredit
                    .handle(
                        new AccountCreditInput(
                            cancelTicketPurchaseInput.getCustomerId(),
                            ticketCustomerRelationship.getTicket().getPrice()))
                    .flatMap(
                        account ->
                            ticketsCustomerRelationshipRepository.delete(
                                ticketCustomerRelationship)));
  }
}
