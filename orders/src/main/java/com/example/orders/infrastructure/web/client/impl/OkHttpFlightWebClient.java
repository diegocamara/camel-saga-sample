package com.example.orders.infrastructure.web.client.impl;

import com.example.orders.domain.model.Order;
import com.example.orders.infrastructure.web.client.FlightWebClient;
import com.example.orders.infrastructure.web.model.BuyTicketResponse;
import com.example.orders.infrastructure.web.model.TicketWebModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class OkHttpFlightWebClient implements FlightWebClient {

  @Override
  public BuyTicketResponse buyTicket(Order order, UUID transactionId) {
    final var buyTicketResponse = new BuyTicketResponse();
    final var ticketWebModel = new TicketWebModel();
    ticketWebModel.setId(UUID.randomUUID());
    buyTicketResponse.setTicket(ticketWebModel);
    return buyTicketResponse;
  }

  @Override
  public void cancelTicket(String order) {
    order.toUpperCase();
  }
}
