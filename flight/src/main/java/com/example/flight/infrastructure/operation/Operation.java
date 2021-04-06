package com.example.flight.infrastructure.operation;

import lombok.Data;

import java.util.UUID;

@Data
public abstract class Operation<OUTPUT> {
  private final UUID id;
  private final OUTPUT output;
  private Status status;
}
