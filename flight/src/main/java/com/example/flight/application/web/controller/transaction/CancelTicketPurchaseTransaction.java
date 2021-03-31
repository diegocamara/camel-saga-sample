package com.example.flight.application.web.controller.transaction;

import com.example.flight.application.web.model.CancelTicketPurchaseRequest;
import com.example.flight.domain.feature.CancelTicketPurchase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class CancelTicketPurchaseTransaction {

  private final CancelTicketPurchase cancelTicketPurchase;

  @Transactional
  public Mono<ResponseEntity<?>> execute(CancelTicketPurchaseRequest cancelTicketPurchaseRequest) {
    return cancelTicketPurchase
        .handle(cancelTicketPurchaseRequest.toCancelTicketPurchaseInput())
        .map(unused -> ResponseEntity.ok().build());
  }
}
