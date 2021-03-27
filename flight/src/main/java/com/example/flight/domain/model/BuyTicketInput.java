package com.example.flight.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BuyTicketInput {
  private UUID ticketId;
  private UUID customerId;
}
