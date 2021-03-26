package com.example.flight.application.web.model;

import lombok.Data;

import java.util.UUID;

@Data
public class BuyTicketRequest {
  private UUID customer;
  private String from;
  private String destination;
}
