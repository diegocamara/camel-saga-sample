package com.example.flight.application.web.controller;

import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.application.web.model.TicketResponse;
import com.example.flight.domain.feature.BuyTicket;
import com.example.flight.domain.feature.DeleteTicketById;
import com.example.flight.domain.feature.FindTicketById;
import com.example.flight.domain.model.Customer;
import com.example.flight.infrasctructure.repository.OperationRepository;
import com.example.flight.infrasctructure.repository.table.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/tickets")
public class TicketsController {

  private final BuyTicket buyTicket;
  private final FindTicketById findTicketById;
  private final DeleteTicketById deleteTicketById;
  private final OperationRepository operationRepository;

  @PostMapping
  @Transactional
  public Mono<ResponseEntity<TicketResponse>> buyTicket(
      @RequestHeader("transaction-reference") UUID transactionReference,
      @RequestBody BuyTicketRequest buyTicketRequest) {
    return buyTicket
        .handle(
            new Customer(buyTicketRequest.getCustomer()),
            buyTicketRequest.getFrom(),
            buyTicketRequest.getDestination())
        .flatMap(
            ticket ->
                operationRepository
                    .create(transactionReference, Operation.BUY_TICKET, ticket)
                    .map(
                        operation ->
                            ResponseEntity.status(HttpStatus.CREATED)
                                .body(new TicketResponse(ticket))));

    //    return operationRepository
    //        .create(transactionReference, Operation.BUY_TICKET)
    //        .then(
    //            buyTicket.handle(
    //                new Customer(buyTicketRequest.getCustomer()),
    //                buyTicketRequest.getFrom(),
    //                buyTicketRequest.getDestination()))
    //        .map(ticket -> ResponseEntity.status(HttpStatus.CREATED).body(new
    // TicketResponse(ticket)))
    //        .onErrorResume(
    //            throwable ->
    //                operationRepository
    //                    .findById(transactionReference)
    //                    .map(
    //                        operationTable ->
    //                            ResponseEntity.ok(new
    // TicketResponse(operationTable.getTicket()))));
  }

  @DeleteMapping("/{customerId}")
  public Mono<ResponseEntity<?>> cancelTicket(@PathVariable("customerId") UUID customerId) {
    return deleteTicketById.handle(customerId).then(Mono.just(ResponseEntity.ok().build()));
  }
}
