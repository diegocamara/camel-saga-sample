package com.example.hotel.infrasctructure.repository.impl;

import com.example.hotel.application.web.model.BookingResponse;
import com.example.hotel.infrasctructure.operation.OperationAlreadyExistsException;
import com.example.hotel.infrasctructure.operation.OperationsRepository;
import com.example.hotel.infrasctructure.operation.Status;
import com.example.hotel.infrasctructure.operation.transaction.booking.BookingOperation;
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
    implements OperationsRepository<BookingOperation> {

  private final ObjectMapper objectMapper;
  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  @Override
  @SneakyThrows
  public Mono<Void> save(BookingOperation operation) {
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
  public Mono<BookingOperation> findByOperationReference(UUID operationReference) {
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql("SELECT * FROM operations WHERE id = :operationReference")
        .bind("operationReference", operationReference)
        .map(this::operation)
        .first();
  }

  @Override
  @SneakyThrows
  public Mono<Void> update(BookingOperation operation) {
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
  private BookingOperation operation(Row row, RowMetadata rowMetadata) {
    final var output =
        objectMapper.readValue(row.get("output_field", String.class), BookingResponse.class);
    final var buyTicketOperation = new BookingOperation(row.get("id", UUID.class), output);
    buyTicketOperation.setStatus(Status.valueOf(row.get("status", String.class)));
    return buyTicketOperation;
  }
}
