package com.example.flight.application.web.controller;

import com.example.flight.application.web.controller.transaction.BuyTicketTransaction;
import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.application.web.model.CancelTicketPurchaseRequest;
import com.example.flight.domain.feature.CancelTicketPurchase;
import com.example.flight.domain.feature.FindTicketCustomerRelationshipById;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/tickets")
public class TicketsController {

  private final BuyTicketTransaction buyTicketTransaction;
  private final FindTicketCustomerRelationshipById findTicketCustomerRelationshipById;
  private final CancelTicketPurchase cancelTicketPurchase;

  @PostMapping
  public Mono<ResponseEntity<BuyTicketResponse>> buyTicket(
      @RequestHeader("transaction-reference") UUID transactionReference,
      @RequestBody BuyTicketRequest buyTicketRequest) {
    return buyTicketTransaction
        .execute(buyTicketRequest, transactionReference)
        .onErrorResume(
            throwable ->
                findTicketCustomerRelationshipById
                    .handle(buyTicketRequest.getTicketId(), buyTicketRequest.getCustomerId())
                    .map(
                        ticketCustomerRelationship ->
                            ResponseEntity.ok(new BuyTicketResponse(ticketCustomerRelationship))));
  }

  @DeleteMapping
  public Mono<ResponseEntity<?>> cancelTicketPurchase(
      @RequestBody CancelTicketPurchaseRequest cancelTicketPurchaseRequest) {
    return cancelTicketPurchase
        .handle(cancelTicketPurchaseRequest.toCancelTicketPurchaseInput())
        .map(unused -> ResponseEntity.ok().build());
  }
}
