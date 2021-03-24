package com.example.credit.infrasctructure.repository.reactive;

import com.example.credit.infrasctructure.repository.table.AccountTable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ReactiveCreditRepository extends ReactiveCrudRepository<AccountTable, UUID> {}
