package com.example.flight.infrastructure.repository.reactive;

import com.example.flight.infrastructure.repository.table.OperationTable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ReactiveOperationsRepository
    extends ReactiveCrudRepository<OperationTable, UUID> {}
