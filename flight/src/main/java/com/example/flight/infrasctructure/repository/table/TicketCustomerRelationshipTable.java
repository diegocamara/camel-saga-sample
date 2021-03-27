package com.example.flight.infrasctructure.repository.table;

import com.example.flight.domain.model.TicketCustomerRelationship;
import lombok.Data;

import java.util.UUID;

@Data
public class TicketCustomerRelationshipTable {

  private UUID ticketId;
  private UUID customerId;

  public TicketCustomerRelationshipTable(TicketCustomerRelationship ticketCustomerRelationship) {
    this.ticketId = ticketCustomerRelationship.getTicket().getId();
    this.customerId = ticketCustomerRelationship.getCustomer().getId();
  }
}
