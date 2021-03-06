package com.example.flight.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Account {
  private final UUID id;
  private final Details details;
}
