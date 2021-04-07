package com.example.hotel.infrasctructure.operation;

import lombok.Data;

import java.util.UUID;

@Data
public abstract class Operation<OUTPUT> {
  private final UUID id;
  private final OUTPUT output;
  private Status status;

  public boolean isExecuted() {
    return Status.EXECUTED.equals(this.getStatus());
  }

  public boolean isRollback() {
    return Status.ROLLBACK.equals(this.getStatus());
  }
}
