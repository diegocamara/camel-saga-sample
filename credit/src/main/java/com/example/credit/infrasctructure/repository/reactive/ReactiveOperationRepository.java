package com.example.credit.infrasctructure.repository.reactive;

import com.example.credit.infrasctructure.repository.table.OperationTable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ReactiveOperationRepository extends ReactiveCrudRepository<OperationTable, UUID> {}
