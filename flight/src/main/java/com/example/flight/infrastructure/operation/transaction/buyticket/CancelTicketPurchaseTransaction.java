package com.example.flight.infrastructure.operation.transaction.buyticket;

import com.example.flight.domain.feature.CancelTicketPurchase;
import com.example.flight.domain.model.CancelTicketPurchaseInput;
import com.example.flight.infrastructure.operation.OperationNotRegisteredException;
import com.example.flight.infrastructure.operation.OperationsRepository;
import com.example.flight.infrastructure.operation.Status;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CancelTicketPurchaseTransaction {

  private final CancelTicketPurchase cancelTicketPurchase;
  private final OperationsRepository<BuyTicketOperation> buyTicketOperationsRepository;

  @Transactional
  public Mono<BuyTicketOperation> execute(UUID operationReference) {
    return buyTicketOperationsRepository
        .findByOperationReference(operationReference)
        .switchIfEmpty(Mono.error(OperationNotRegisteredException::new))
        .filter(BuyTicketOperation::isExecuted)
        .flatMap(
            buyTicketOperation -> {
              final var buyTicketResponse = buyTicketOperation.getOutput();
              final var ticketId = buyTicketResponse.getTicket().getId();
              final var customerId = buyTicketResponse.getCustomer().getId();
              final var cancelTicketPurchaseInput =
                  new CancelTicketPurchaseInput(ticketId, customerId);
              buyTicketOperation.setStatus(Status.ROLLBACK);
              return cancelTicketPurchase
                  .handle(cancelTicketPurchaseInput)
                  .then(
                      buyTicketOperationsRepository
                          .update(buyTicketOperation)
                          .thenReturn(buyTicketOperation));
            });
  }
}
