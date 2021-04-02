package com.example.orders.infrastructure.web.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuyTicketResponse {
  private TicketWebModel ticket;
  private CustomerWebModel customer;
}
