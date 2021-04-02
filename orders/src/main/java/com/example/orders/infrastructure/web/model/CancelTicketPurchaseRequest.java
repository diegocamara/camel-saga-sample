package com.example.orders.infrastructure.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CancelTicketPurchaseRequest {
  private UUID ticketId;
  private UUID customerId;
}
