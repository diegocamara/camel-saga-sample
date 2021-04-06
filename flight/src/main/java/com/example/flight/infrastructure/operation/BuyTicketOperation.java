package com.example.flight.infrastructure.operation;

import com.example.flight.application.web.model.BuyTicketResponse;

import java.util.UUID;

public class BuyTicketOperation extends Operation<BuyTicketResponse> {

  public BuyTicketOperation(UUID id, BuyTicketResponse buyTicketResponse) {
    super(id, buyTicketResponse);
  }
}
