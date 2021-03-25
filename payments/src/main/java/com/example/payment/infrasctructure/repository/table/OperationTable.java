package com.example.payment.infrasctructure.repository.table;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("OPERATIONS")
@NoArgsConstructor
public class OperationTable {
  @Id private UUID id;

  @Column("OPERATION_NAME")
  private Operation operation;

  private AccountTable account;

  public OperationTable(UUID id, Operation operation) {
    this.id = id;
    this.operation = operation;
  }
}
