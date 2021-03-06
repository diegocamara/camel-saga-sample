package com.example.flight.infrastructure.repository.impl;

import com.example.flight.application.web.model.BuyTicketResponse;
import com.example.flight.infrastructure.operation.OperationAlreadyExistsException;
import com.example.flight.infrastructure.operation.OperationsRepository;
import com.example.flight.infrastructure.operation.Status;
import com.example.flight.infrastructure.operation.transaction.buyticket.BuyTicketOperation;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DataIntegrityViolationException;
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

  @Override
  @SneakyThrows
  public Mono<Void> save(BuyTicketOperation operation) {
    final var output = objectMapper.writeValueAsString(operation.getOutput());
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql(
            "INSERT INTO operations (id, status, output_field) VALUES (:id, :status, :outputField)")
        .bind("id", operation.getId())
        .bind("status", operation.getStatus().toString())
        .bind("outputField", output)
        .fetch()
        .rowsUpdated()
        .then()
        .onErrorResume(
            throwable ->
                DataIntegrityViolationException.class.isAssignableFrom(throwable.getClass())
                    ? Mono.error(OperationAlreadyExistsException::new)
                    : Mono.error(throwable));
  }

  @Override
  public Mono<BuyTicketOperation> findByOperationReference(UUID operationReference) {
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql("SELECT * FROM operations WHERE id = :operationReference")
        .bind("operationReference", operationReference)
        .map(this::operation)
        .first();
  }

  @Override
  @SneakyThrows
  public Mono<Void> update(BuyTicketOperation operation) {
    final var output = objectMapper.writeValueAsString(operation.getOutput());
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql("UPDATE operations SET status = :status, output_field = :outputField WHERE id = :id")
        .bind("id", operation.getId())
        .bind("status", operation.getStatus().toString())
        .bind("outputField", output)
        .fetch()
        .rowsUpdated()
        .then();
  }

  @SneakyThrows
  private BuyTicketOperation operation(Row row, RowMetadata rowMetadata) {
    final var output =
        objectMapper.readValue(row.get("output_field", String.class), BuyTicketResponse.class);
    final var buyTicketOperation = new BuyTicketOperation(row.get("id", UUID.class), output);
    buyTicketOperation.setStatus(Status.valueOf(row.get("status", String.class)));
    return buyTicketOperation;
  }
}
