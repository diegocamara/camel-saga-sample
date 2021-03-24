package com.example.credit.infrasctructure.repository.impl;

import com.example.credit.domain.model.Account;
import com.example.credit.domain.model.AccountRepository;
import com.example.credit.domain.model.Client;
import com.example.credit.infrasctructure.repository.reactive.ReactiveCreditRepository;
import com.example.credit.infrasctructure.repository.table.AccountTable;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class R2DBCEntityTemplateAccountRepository implements AccountRepository {

  private final ReactiveCreditRepository reactiveCreditRepository;
  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  @Override
  public Mono<Void> save(Account account) {
    return r2dbcEntityTemplate.insert(new AccountTable(account)).then();
  }

  @Override
  public Mono<Void> update(Account account) {
    return reactiveCreditRepository.save(new AccountTable(account)).then();
  }

  @Override
  public Mono<Account> findByClient(Client client) {
    return reactiveCreditRepository.findById(client.getId()).map(this::credit);
  }

  private Account credit(AccountTable accountTable) {
    final var credit = new Account();
    credit.setClient(new Client(accountTable.getId()));
    credit.setUsed(accountTable.getUsed());
    credit.setMaxLimit(accountTable.getMaxLimit());
    return credit;
  }
}
