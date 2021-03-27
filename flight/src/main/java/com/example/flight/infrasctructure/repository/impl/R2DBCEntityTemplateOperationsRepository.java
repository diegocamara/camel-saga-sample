package com.example.flight.infrasctructure.repository.impl;

import com.example.flight.infrasctructure.repository.OperationsRepository;
import com.example.flight.infrasctructure.repository.table.Operation;
import com.example.flight.infrasctructure.repository.table.OperationTable;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class R2DBCEntityTemplateOperationsRepository implements OperationsRepository {

  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  @Override
  public Mono<OperationTable> create(UUID id, Operation operation) {
    return r2dbcEntityTemplate
        .insert(new OperationTable(id, operation))
        .map(operationTable -> operationTable);
  }

  private OperationTable operationTable(Row row) {
    final var operationTable = new OperationTable();
    operationTable.setId(row.get("id", UUID.class));
    operationTable.setOperation(Operation.valueOf(row.get("operation_name", String.class)));
    return operationTable;
  }
}
