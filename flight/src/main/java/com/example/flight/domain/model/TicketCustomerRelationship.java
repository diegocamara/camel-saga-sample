package com.example.flight.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketCustomerRelationship {
  private Ticket ticket;
  private Customer customer;
}
