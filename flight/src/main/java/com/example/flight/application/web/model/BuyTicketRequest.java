package com.example.flight.application.web.model;

import com.example.flight.domain.model.BuyTicketInput;
import lombok.Data;

import java.util.UUID;

@Data
public class BuyTicketRequest {
  private UUID ticketId;
  private UUID customerId;

  public BuyTicketInput toBuyTicketInput() {
    return new BuyTicketInput(this.ticketId, this.customerId);
  }
}
