package com.example.payment.infrasctructure.repository.impl;

import com.example.payment.domain.model.Account;
import com.example.payment.domain.model.AccountRepository;
import com.example.payment.domain.model.Client;
import com.example.payment.infrasctructure.repository.reactive.ReactiveAccountRepository;
import com.example.payment.infrasctructure.repository.table.AccountTable;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class R2DBCEntityTemplateAccountRepository implements AccountRepository {

  private final ReactiveAccountRepository reactiveAccountRepository;
  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  @Override
  public Mono<Void> save(Account account) {
    return r2dbcEntityTemplate.insert(new AccountTable(account)).then();
  }

  @Override
  public Mono<Void> update(Account account) {
    return reactiveAccountRepository.save(new AccountTable(account)).then();
  }

  @Override
  public Mono<Account> findByClient(Client client) {
    return reactiveAccountRepository.findById(client.getId()).map(this::account);
  }

  private Account account(AccountTable accountTable) {
    final var account = new Account();
    account.setClient(new Client(accountTable.getId()));
    account.setUsed(accountTable.getUsed());
    account.setMaxLimit(accountTable.getMaxLimit());
    return account;
  }
}
