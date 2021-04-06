package com.example.flight.infrastructure.repository.table;

import com.example.flight.infrastructure.operation.Operation;
import com.example.flight.infrastructure.operation.Status;
import io.r2dbc.postgresql.codec.Json;
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

  @Column("status")
  private Status status;

  @Column("output_field")
  private Json output;

  public OperationTable(UUID id, Status status, Json output) {
    this.id = id;
    this.status = status;
    this.output = output;
  }

  public OperationTable(Operation<?> operation, Json output) {
    this.id = operation.getId();
    this.status = operation.getStatus();
    this.output = output;
  }
}
