package com.example.orders.application.web.model;

import com.example.orders.domain.model.FlightTicketPurchase;
import lombok.Data;

import java.util.UUID;

@Data
public class FlightTicketPurchaseWebModel {
  private UUID ticketId;

  public FlightTicketPurchase toFlightTicketPurchase() {
    return new FlightTicketPurchase(this.getTicketId());
  }
}
