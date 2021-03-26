package com.example.flight.infrasctructure.repository.impl;

import com.example.flight.domain.model.Ticket;
import com.example.flight.infrasctructure.repository.OperationRepository;
import com.example.flight.infrasctructure.repository.table.Operation;
import com.example.flight.infrasctructure.repository.table.OperationTable;
import com.example.flight.infrasctructure.repository.table.TicketTable;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@AllArgsConstructor
public class R2DBCEntityTemplateOperationRepository implements OperationRepository {

  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  @Override
  public Mono<OperationTable> findById(UUID id) {
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql(
            "SELECT * FROM operations AS operations"
                + " INNER JOIN tickets AS tickets ON tickets.id = operations.ticket_id"
                + " WHERE operations.id = :operationId")
        .bind("operationId", id)
        .map(row -> row)
        .first()
        .map(this::operationTable);
  }

  @Override
  public Mono<OperationTable> create(UUID id, Operation operation, Ticket ticket) {
    return r2dbcEntityTemplate
        .insert(new OperationTable(id, operation, new TicketTable(ticket)))
        .map(operationTable -> operationTable);
  }

  private OperationTable operationTable(Row row) {
    final var operationTable = new OperationTable();
    operationTable.setId(row.get("id", UUID.class));
    operationTable.setOperation(Operation.valueOf(row.get("operation_name", String.class)));

    final var ticketTable = new TicketTable();
    //    ticketTable.setId(row.get());

    return operationTable;
  }
}
