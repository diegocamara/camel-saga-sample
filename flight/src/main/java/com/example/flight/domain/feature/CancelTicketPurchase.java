package com.example.flight.domain.feature;

import com.example.flight.domain.model.CancelTicketPurchaseInput;
import reactor.core.publisher.Mono;

public interface CancelTicketPurchase {
  Mono<Void> handle(CancelTicketPurchaseInput cancelTicketPurchaseInput);
}
