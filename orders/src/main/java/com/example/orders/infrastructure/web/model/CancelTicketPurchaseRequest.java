package com.example.orders.infrastructure.web.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CancelTicketPurchaseRequest {
  private UUID ticketId;
  private UUID customerId;
}
