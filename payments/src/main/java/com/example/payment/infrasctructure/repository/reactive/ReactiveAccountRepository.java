package com.example.payment.infrasctructure.repository.reactive;

import com.example.payment.infrasctructure.repository.table.AccountTable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ReactiveAccountRepository extends ReactiveCrudRepository<AccountTable, UUID> {}
