package com.example.flight.infrastructure.operation.transaction.buyticket;

import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.infrastructure.operation.Operation;

import java.util.UUID;

public class BuyTicketOperation extends Operation<BuyTicketResponse> {

  public BuyTicketOperation(UUID id, BuyTicketResponse buyTicketResponse) {
    super(id, buyTicketResponse);
  }
}
