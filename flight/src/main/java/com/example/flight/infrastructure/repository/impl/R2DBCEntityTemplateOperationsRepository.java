package com.example.flight.infrastructure.repository.impl;

import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.infrastructure.operation.BuyTicketOperation;
import com.example.flight.infrastructure.operation.Status;
import com.example.flight.infrastructure.repository.OperationsRepository;
import com.example.flight.infrastructure.repository.reactive.ReactiveOperationsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class R2DBCEntityTemplateOperationsRepository
    implements OperationsRepository<BuyTicketOperation> {

  private final ObjectMapper objectMapper;
  private final R2dbcEntityTemplate r2dbcEntityTemplate;
  private final ReactiveOperationsRepository reactiveOperationsRepository;

  @Override
  @SneakyThrows
  public Mono<Void> save(BuyTicketOperation operation) {
    final var output = objectMapper.writeValueAsString(operation.getOutput());
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql(
            "INSERT INTO operations (id, status, output_field) VALUES (:id, :status, :output_field)")
        .bind("id", operation.getId().toString())
        .bind("status", operation.getStatus().toString())
        .bind("output_field", output)
        .fetch()
        .rowsUpdated()
        .then();
  }

  @Override
  public Mono<BuyTicketOperation> findByOperationReference(UUID operationReference) {
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql("SELECT * FROM operations WHERE id = :operationReference")
        .bind("operationReference", operationReference.toString())
        .map(this::operation)
        .first();
  }

  @SneakyThrows
  private BuyTicketOperation operation(Row row, RowMetadata rowMetadata) {
    final var output =
        objectMapper.readValue(row.get("output_field", String.class), BuyTicketResponse.class);
    final var buyTicketOperation = new BuyTicketOperation(row.get("id", UUID.class), output);
    buyTicketOperation.setStatus(Status.valueOf(row.get("status", String.class)));
    return buyTicketOperation;
  }

  //  @Override
  //  public Mono<OperationTable> create(UUID id, Operation operation) {
  //    return r2dbcEntityTemplate
  //        .insert(new OperationTable(id, operation))
  //        .map(operationTable -> operationTable);
  //  }
  //
  //  private OperationTable operationTable(Row row) {
  //    final var operationTable = new OperationTable();
  //    operationTable.setId(row.get("id", UUID.class));
  //    operationTable.setOperation(Operation.valueOf(row.get("operation_name", String.class)));
  //    return operationTable;
  //  }
}
