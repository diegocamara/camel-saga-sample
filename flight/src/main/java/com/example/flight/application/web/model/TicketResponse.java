package com.example.flight.application.web.model;

import com.example.flight.domain.model.Ticket;
import com.example.flight.infrasctructure.repository.table.TicketTable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TicketResponse {
  private UUID id;
  private UUID customer;
  private String from;
  private String destination;

  public TicketResponse(Ticket ticket) {
    this.id = ticket.getId();
    this.customer = ticket.getCustomer().getId();
    this.from = ticket.getFrom();
    this.destination = ticket.getDestination();
  }

  public TicketResponse(TicketTable ticket) {
    this.id = ticket.getId();
    this.customer = ticket.getCustomerId();
    this.from = ticket.getFrom();
    this.destination = ticket.getDestination();
  }
}
