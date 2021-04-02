package com.example.orders.infrastructure.web.client;

import com.example.orders.infrastructure.web.model.BuyTicketRequest;
import com.example.orders.infrastructure.web.model.BuyTicketResponse;
import com.example.orders.infrastructure.web.model.CancelTicketPurchaseRequest;

import java.util.UUID;

public interface FlightWebClient {
  BuyTicketResponse buyTicket(BuyTicketRequest buyTicketRequest, UUID transactionId);

  void cancelTicket(CancelTicketPurchaseRequest cancelTicketPurchaseRequest);
}
