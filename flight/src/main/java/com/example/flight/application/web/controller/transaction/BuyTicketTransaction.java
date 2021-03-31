package com.example.flight.application.web.controller.transaction;

import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.domain.feature.BuyTicket;
import com.example.flight.infrasctructure.repository.OperationsRepository;
import com.example.flight.infrasctructure.repository.table.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BuyTicketTransaction {

  private final BuyTicket buyTicket;
  private final OperationsRepository operationsRepository;

  @Transactional
  public Mono<ResponseEntity<BuyTicketResponse>> execute(
      BuyTicketRequest buyTicketRequest, UUID transactionReference) {
    return operationsRepository
        .create(transactionReference, Operation.BUY_TICKET)
        .flatMap(
            operationTable ->
                buyTicket
                    .handle(buyTicketRequest.toBuyTicketInput())
                    .map(
                        ticketCustomerRelationship ->
                            ResponseEntity.status(HttpStatus.CREATED)
                                .body(new BuyTicketResponse(ticketCustomerRelationship))));
  }
}
