package com.example.flight.application.web.model;

import com.example.flight.domain.model.Ticket;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TicketWebModel {
  private UUID id;
  private BigDecimal price;
  private String from;
  private String destination;

  public TicketWebModel(Ticket ticket) {
    this.id = ticket.getId();
    this.price = ticket.getPrice();
    this.from = ticket.getFrom();
    this.destination = ticket.getDestination();
  }
}
