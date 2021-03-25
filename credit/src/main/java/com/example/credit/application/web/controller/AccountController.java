package com.example.credit.application.web.controller;

import com.example.credit.application.web.model.DebitRequest;
import com.example.credit.application.web.model.DebitResponse;
import com.example.credit.domain.feature.AccountDebit;
import com.example.credit.domain.feature.FindAccountById;
import com.example.credit.domain.model.Client;
import com.example.credit.infrasctructure.repository.OperationRepository;
import com.example.credit.infrasctructure.repository.table.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/account")
public class AccountController {

  private final OperationRepository operationRepository;
  private final AccountDebit accountDebit;
  private final FindAccountById findAccountById;

  //  @Transactional
  @PatchMapping("/{clientId}/consume")
  public Mono<ResponseEntity<DebitResponse>> debit(
      @PathVariable("clientId") UUID clientId,
      @RequestHeader("transaction-reference") UUID transactionReference,
      @RequestBody DebitRequest debitRequest) {

    return operationRepository
        .create(transactionReference, Operation.CREDIT)
        .then(accountDebit.handle(new Client(clientId), debitRequest.getAmount()))
        .map(account -> ResponseEntity.ok(new DebitResponse(account, transactionReference)))
        .onErrorResume(
            throwable ->
                findAccountById
                    .handle(clientId)
                    .map(
                        account ->
                            ResponseEntity.ok(new DebitResponse(account, transactionReference))));
  }
}
