package com.example.flight.application.web.controller.transaction.buyticket;

import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.domain.feature.BuyTicket;
import com.example.flight.infrastructure.operation.OperationsRepository;
import com.example.flight.infrastructure.operation.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BuyTicketTransaction {

  private final BuyTicket buyTicket;
  private final OperationsRepository<BuyTicketOperation> buyTicketOperationsRepository;

  @Transactional
  public Mono<BuyTicketOperation> execute(
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
                    }));
  }
}
