package com.example.flight.infrasctructure.repository.reactive;

import com.example.flight.infrasctructure.repository.table.TicketTable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ReactiveTicketsRepository extends ReactiveCrudRepository<TicketTable, UUID> {}
