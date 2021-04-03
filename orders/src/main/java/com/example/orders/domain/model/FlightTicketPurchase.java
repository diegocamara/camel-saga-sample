package com.example.orders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FlightTicketPurchase {
  private UUID ticketId;
}
