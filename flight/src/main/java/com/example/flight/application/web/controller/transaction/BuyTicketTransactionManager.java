package com.example.flight.application.web.controller.transaction;

import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.domain.feature.BuyTicket;
import com.example.flight.infrastructure.operation.BuyTicketOperation;
import com.example.flight.infrastructure.operation.Status;
import com.example.flight.infrastructure.repository.OperationsRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BuyTicketTransactionManager {

  private final BuyTicket buyTicket;
  private final OperationsRepository<BuyTicketOperation> buyTicketOperationsRepository;

  @Transactional
  public Mono<ResponseEntity<BuyTicketResponse>> execute(
      BuyTicketRequest buyTicketRequest, UUID operationReference) {
    return buyTicketOperationsRepository
        .findByOperationReference(operationReference)
        .switchIfEmpty(
            buyTicket
                .handle(buyTicketRequest.toBuyTicketInput())
                .flatMap(
                    ticketCustomerRelationship -> {
                      final var buyTicketResponse =
                          new BuyTicketResponse(ticketCustomerRelationship);
                      final var buyTicketOperation =
                          new BuyTicketOperation(operationReference, buyTicketResponse);
                      buyTicketOperation.setStatus(Status.EXECUTED);
                      return buyTicketOperationsRepository
                          .save(buyTicketOperation)
                          .thenReturn(buyTicketOperation);
                    }))
        .onErrorResume(
            throwable -> buyTicketOperationsRepository.findByOperationReference(operationReference))
        .map(
            buyTicketOperation ->
                ResponseEntity.status(HttpStatus.CREATED).body(buyTicketOperation.getOutput()));
  }
}
