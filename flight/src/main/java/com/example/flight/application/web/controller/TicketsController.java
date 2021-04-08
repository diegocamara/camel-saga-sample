package com.example.flight.application.web.controller;

import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.infrastructure.operation.transaction.buyticket.BuyTicketTransactionManager;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/tickets")
public class TicketsController {

  public static final String OPERATION_REFERENCE_HEADER = "operation-reference";
  private final BuyTicketTransactionManager buyTicketTransactionManager;

  @PostMapping
  public Mono<ResponseEntity<BuyTicketResponse>> buyTicket(
      @RequestHeader(OPERATION_REFERENCE_HEADER) UUID operationReference,
      @RequestBody BuyTicketRequest buyTicketRequest) {
    return buyTicketTransactionManager
        .execute(buyTicketRequest, operationReference)
        .map(
            buyTicketOperation ->
                ResponseEntity.status(HttpStatus.CREATED).body(buyTicketOperation.getOutput()));
  }

  @DeleteMapping
  public Mono<ResponseEntity<?>> cancelTicketPurchase(
      @RequestHeader(OPERATION_REFERENCE_HEADER) UUID operationReference) {
    return buyTicketTransactionManager
        .rollback(operationReference)
        .map(unused -> ResponseEntity.ok().build());
  }
}
