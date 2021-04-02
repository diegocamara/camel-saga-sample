package com.example.orders.infrastructure.web.model;

import lombok.Data;

import java.util.UUID;

@Data
public class BuyTicketRequest {
  private UUID ticketId;
  private UUID customerId;
}
