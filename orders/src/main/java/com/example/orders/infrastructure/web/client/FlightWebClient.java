package com.example.orders.infrastructure.web.client;

import com.example.orders.domain.model.Order;
import com.example.orders.infrastructure.web.model.BuyTicketResponse;

import java.util.UUID;

public interface FlightWebClient {
  BuyTicketResponse buyTicket(Order order, UUID transactionId);

  void cancelTicket(String order);
}
