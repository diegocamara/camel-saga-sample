package com.example.payment.infrasctructure.repository;

import com.example.payment.infrasctructure.repository.table.Operation;
import com.example.payment.infrasctructure.repository.table.OperationTable;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OperationRepository {

  Mono<OperationTable> findById(UUID id);

  Mono<OperationTable> create(UUID id, Operation operation);
}
