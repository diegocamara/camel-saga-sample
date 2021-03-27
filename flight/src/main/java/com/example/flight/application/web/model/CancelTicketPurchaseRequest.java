package com.example.flight.application.web.model;

import com.example.flight.domain.model.CancelTicketPurchaseInput;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CancelTicketPurchaseRequest {
  private UUID ticketId;
  private UUID customerId;

  public CancelTicketPurchaseInput toCancelTicketPurchaseInput() {
    return new CancelTicketPurchaseInput(this.ticketId, this.customerId);
  }
}
