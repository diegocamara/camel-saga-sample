package com.example.flight.application.web.controller;

import com.example.flight.application.web.controller.transaction.BuyTicketTransactionManager;
import com.example.flight.application.web.controller.transaction.CancelTicketPurchaseTransaction;
import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.application.web.model.CancelTicketPurchaseRequest;
import com.example.flight.domain.feature.FindTicketCustomerRelationshipById;
import com.example.flight.infrastructure.aspect.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/tickets")
public class TicketsController {

  private final BuyTicketTransactionManager buyTicketTransactionManager;
  private final CancelTicketPurchaseTransaction cancelTicketPurchaseTransaction;
  private final FindTicketCustomerRelationshipById findTicketCustomerRelationshipById;

  @Operation
  @PostMapping
  public Mono<ResponseEntity<BuyTicketResponse>> buyTicket(
      @RequestHeader("operation-reference") UUID operationReference,
      @RequestBody BuyTicketRequest buyTicketRequest) {
    return buyTicketTransactionManager.execute(buyTicketRequest, operationReference);
  }

  @DeleteMapping
  public Mono<ResponseEntity<?>> cancelTicketPurchase(
      @RequestBody CancelTicketPurchaseRequest cancelTicketPurchaseRequest) {
    return cancelTicketPurchaseTransaction.execute(cancelTicketPurchaseRequest);
  }
}
