package com.example.flight.domain.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Ticket {
  private UUID id;
  private String from;
  private String destination;
}
