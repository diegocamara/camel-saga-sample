package com.example.flight.infrastructure.operation.transaction.buyticket;

import com.example.flight.application.web.model.BuyTicketRequest;
import com.example.flight.infrastructure.operation.OperationAlreadyExistsException;
import com.example.flight.infrastructure.operation.OperationsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BuyTicketTransactionManager {

  private final BuyTicketTransaction buyTicketTransaction;
  private final CancelTicketPurchaseTransaction cancelTicketPurchaseTransaction;
  private final OperationsRepository<BuyTicketOperation> buyTicketOperationsRepository;

  public Mono<BuyTicketOperation> execute(
      BuyTicketRequest buyTicketRequest, UUID operationReference) {
    return buyTicketTransaction
        .execute(buyTicketRequest, operationReference)
        .onErrorResume(
            throwable ->
                OperationAlreadyExistsException.class.isAssignableFrom(throwable.getClass())
                    ? buyTicketOperationsRepository.findByOperationReference(operationReference)
                    : Mono.error(throwable));
  }

  public Mono<Void> rollback(UUID operationReference) {
    return cancelTicketPurchaseTransaction.execute(operationReference).then();
  }
}
