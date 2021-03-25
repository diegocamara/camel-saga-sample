package com.example.credit.infrasctructure.repository.impl;

import com.example.credit.infrasctructure.repository.OperationRepository;
import com.example.credit.infrasctructure.repository.reactive.ReactiveOperationRepository;
import com.example.credit.infrasctructure.repository.table.AccountTable;
import com.example.credit.infrasctructure.repository.table.Operation;
import com.example.credit.infrasctructure.repository.table.OperationTable;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class R2DBCEntityTemplateOperationRepository implements OperationRepository {

  private final ReactiveOperationRepository reactiveOperationRepository;
  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  @Override
  public Mono<OperationTable> findById(UUID id) {
    return r2dbcEntityTemplate
        .getDatabaseClient()
        .sql(
            "SELECT * FROM operations AS operations"
                + " INNER JOIN accounts AS accounts ON accounts.id = operations.account_id"
                + " WHERE operations.id = :operationId")
        .bind("operationId", id)
        .map(row -> row)
        .first()
        .map(this::operationTable);
  }

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
    final var accountTable = new AccountTable();
    accountTable.setId(row.get("account_id", UUID.class));
    accountTable.setUsed(row.get("used", BigDecimal.class));
    accountTable.setMaxLimit(row.get("max_limit", BigDecimal.class));
    operationTable.setAccount(accountTable);
    return operationTable;
  }
}
