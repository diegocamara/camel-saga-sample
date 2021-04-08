package com.example.orders.infrastructure.web.client;

import com.example.orders.infrastructure.web.model.BuyTicketRequest;
import com.example.orders.infrastructure.web.model.BuyTicketResponse;

import java.util.UUID;

public interface FlightWebClient {
  BuyTicketResponse buyTicket(BuyTicketRequest buyTicketRequest, UUID operationReference);

  void cancelTicket(UUID operationReference);
}
