package com.example.flight.application.web.model;

import com.example.flight.domain.model.TicketCustomerRelationship;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuyTicketResponse {
  private TicketWebModel ticket;
  private CustomerWebModel customer;

  public BuyTicketResponse(TicketCustomerRelationship ticketCustomerRelationship) {
    this.ticket = new TicketWebModel(ticketCustomerRelationship.getTicket());
    this.customer = new CustomerWebModel(ticketCustomerRelationship.getCustomer());
  }
}
