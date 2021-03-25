package com.example.hotel.infrasctructure.repository.reactive;

import com.example.hotel.infrasctructure.repository.table.OperationTable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ReactiveOperationRepository extends ReactiveCrudRepository<OperationTable, UUID> {}
