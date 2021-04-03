package com.example.orders.infrastructure.repository.springdata.document;

import com.example.orders.domain.model.FlightTicketPurchase;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class FlightTicketPurchaseDocument {
  private UUID ticketId;

  public FlightTicketPurchaseDocument(FlightTicketPurchase flightTicketPurchase) {
    this.ticketId = flightTicketPurchase.getTicketId();
  }
}
