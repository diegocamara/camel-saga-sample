package com.example.flight.infrastructure.repository;

import com.example.flight.infrastructure.operation.Operation;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OperationsRepository<T extends Operation<?>> {

  Mono<Void> save(T operation);

  Mono<T> findByOperationReference(UUID operationReference);
}
