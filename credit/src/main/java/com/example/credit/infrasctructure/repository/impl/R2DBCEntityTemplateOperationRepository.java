package com.example.credit.infrasctructure.repository.impl;

import com.example.credit.infrasctructure.repository.OperationRepository;
import com.example.credit.infrasctructure.repository.table.Operation;
import com.example.credit.infrasctructure.repository.table.OperationTable;
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
  public Mono<OperationTable> create(UUID id, Operation operation) {
    return r2dbcEntityTemplate
        .insert(new OperationTable(id, operation))
        .map(operationTable -> operationTable);
  }
}
