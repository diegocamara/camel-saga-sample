package com.example.flight.application.web.model;

import com.example.flight.domain.model.Ticket;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TicketWebModel {
  private UUID id;
  private String from;
  private String destination;

  public TicketWebModel(Ticket ticket) {
    this.id = ticket.getId();
    this.from = ticket.getFrom();
    this.destination = ticket.getDestination();
  }
}
